package ro.code4.monitorizarevot.repositories

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Retrofit
import ro.code4.monitorizarevot.data.AppDatabase
import ro.code4.monitorizarevot.data.model.*
import ro.code4.monitorizarevot.data.model.answers.AnsweredQuestion
import ro.code4.monitorizarevot.data.model.answers.SelectedAnswer
import ro.code4.monitorizarevot.data.model.response.LoginResponse
import ro.code4.monitorizarevot.data.model.response.SyncResponse
import ro.code4.monitorizarevot.data.model.response.VersionResponse
import ro.code4.monitorizarevot.data.pojo.AnsweredQuestionPOJO
import ro.code4.monitorizarevot.data.pojo.BranchDetailsInfo
import ro.code4.monitorizarevot.data.pojo.FormWithSections
import ro.code4.monitorizarevot.data.pojo.SectionWithQuestions
import ro.code4.monitorizarevot.helper.createMultipart
import ro.code4.monitorizarevot.services.ApiInterface
import ro.code4.monitorizarevot.services.LoginInterface
import java.io.File


class Repository : KoinComponent {

    companion object {
        @JvmStatic
        val TAG = Repository::class.java.simpleName
    }

    private val retrofit: Retrofit by inject()
    private val db: AppDatabase by inject()
    private val loginInterface: LoginInterface by lazy {
        retrofit.create(LoginInterface::class.java)
    }
    private val apiInterface: ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }

    fun login(user: User): Observable<LoginResponse> = loginInterface.login(user)

    fun getCounties(): Single<List<County>> {

        val observableApi = apiInterface.getCounties()
        val observableDb = db.countyDao().getAll().take(1).single(emptyList())

        return Single.zip(
            observableDb,
            observableApi,
            BiFunction<List<County>, List<County>, List<County>> { dbCounties, apiCounties ->
                //todo side effects are recommended in "do" methods, check: https://github.com/Froussios/Intro-To-RxJava/blob/master/Part%203%20-%20Taming%20the%20sequence/1.%20Side%20effects.md
                if (dbCounties != apiCounties) {
                    db.countyDao().save(*apiCounties.map { it }.toTypedArray())
                    return@BiFunction apiCounties
                }
                dbCounties

            })
    }

    fun getCounty(countyCode: String): Observable<County> {

        return db.countyDao().get(countyCode).toObservable()
    }

    fun getBranch(countyCode: String, branchNumber: Int): Observable<BranchDetails> {
        return db.branchDetailsDao().get(countyCode, branchNumber).toObservable()
    }

    fun getBranchInfo(countyCode: String, branchNumber: Int): Observable<BranchDetailsInfo> {
        return db.branchDetailsDao().getBranchInfo(countyCode, branchNumber).toObservable()
    }

    fun saveBranchDetails(branchDetails: BranchDetails) {
        Single.fromCallable { db.branchDetailsDao().save(branchDetails) }.toObservable().flatMap {
            postBranchDetails(branchDetails)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    private fun postBranchDetails(branchDetails: BranchDetails): Observable<ResponseBody> =
        apiInterface.postBranchDetails(branchDetails).doOnNext {
            branchDetails.synced = true
            db.branchDetailsDao().updateBranchDetails(branchDetails)
        }


    fun getAnswers(countyCode: String, branchNumber: Int): LiveData<List<AnsweredQuestionPOJO>> =
        db.formDetailsDao().getAnswersFor(countyCode, branchNumber)

    fun getFormsWithQuestions(): LiveData<List<FormWithSections>> =
        db.formDetailsDao().getFormsWithSections()

    fun getSectionsWithQuestions(formCode: String): LiveData<List<SectionWithQuestions>> =
        db.formDetailsDao().getSectionsWithQuestions(formCode)

    fun getForms(): Observable<Unit> {

        val observableDb = db.formDetailsDao().getAllForms().toObservable()

        val observableApi = apiInterface.getForms()

        return Observable.zip(
            observableDb.onErrorReturn { null },
            observableApi.onErrorReturn { null },
            BiFunction<List<FormDetails>?, VersionResponse?, Unit> { dbFormDetails, response ->
                processFormDetailsData(dbFormDetails, response)

            })
    }


    private fun deleteFormDetails(list: List<FormDetails>) {
        db.formDetailsDao().deleteForms(*list.map { it }.toTypedArray())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    private fun deleteFormDetails(formDetails: FormDetails) {
        db.formDetailsDao().deleteForms(formDetails)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe()
    }


    @SuppressLint("CheckResult")
    private fun saveFormDetails(list: List<FormDetails>) {
        db.formDetailsDao().saveForm(*list.map { it }.toTypedArray()).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                list.forEach { getFormQuestions(it) }
            }
    }

    @SuppressLint("CheckResult")
    private fun saveFormDetails(formDetails: FormDetails) {
        db.formDetailsDao().saveForm(formDetails).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                getFormQuestions(formDetails)
            }
    }

    private fun processFormDetailsData(
        dbFormDetails: List<FormDetails>?,
        response: VersionResponse?
    ) {
        if (response == null) {
            return
        }
        val apiFormDetails = response.formDetailsList
        if (dbFormDetails == null || dbFormDetails.isEmpty()) {
            saveFormDetails(apiFormDetails)
            return
        }
        apiFormDetails.forEach { apiForm ->
            if (apiForm.formVersion != dbFormDetails.find { it.code == apiForm.code }?.formVersion) {
                deleteFormDetails(apiForm)
                //TODO delete answers and other bullshits
                saveFormDetails(apiForm)
            }
        }
    }

    private fun getFormQuestions(form: FormDetails) {
        apiInterface.getForm(form.code).doOnNext { list ->
            list.forEach { section ->
                section.formCode = form.code
                section.questions.forEach { question ->
                    question.sectionId = section.id
                    question.answers.forEach { answer -> answer.questionId = question.id }
                }
            }
            db.formDetailsDao().save(*list.map { it }.toTypedArray())
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe()

    }

    fun getAnswersForForm(
        countyCode: String?,
        branchNumber: Int,
        formCode: String
    ): LiveData<List<AnsweredQuestionPOJO>> {
        return db.formDetailsDao().getAnswersForForm(countyCode, branchNumber, formCode)
    }

    fun saveAnsweredQuestion(answeredQuestion: AnsweredQuestion, answers: List<SelectedAnswer>) {
        Observable.create<Unit> {
            db.formDetailsDao().insertAnsweredQuestion(answeredQuestion, answers)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    @SuppressLint("CheckResult")
    fun syncAnswers(countyCode: String, branchNumber: Int, formCode: String) {
        db.formDetailsDao().getNotSyncedQuestionsForForm(countyCode, branchNumber, formCode)
            .toObservable()
            .subscribeOn(Schedulers.io()).flatMap {
                syncAnswers(it)
            }.observeOn(AndroidSchedulers.mainThread()).subscribe({
                if (it.isCompletedSuccessfully) {
                    Observable.create<Unit> {
                        db.formDetailsDao()
                            .updateAnsweredQuestions(countyCode, branchNumber, formCode)
                    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
                }
            }, {
                Log.i(TAG, it.message ?: "Error on synchronizing data")
            })
    }

    private fun syncAnswers(list: List<AnsweredQuestionPOJO>): Observable<SyncResponse> {
        val responseAnswerContainer = ResponseAnswerContainer()
        responseAnswerContainer.responseMapperList = list.map {
            it.answeredQuestion.options = it.selectedAnswers
            return@map it.answeredQuestion
        }
        return apiInterface.postQuestionAnswer(responseAnswerContainer)
    }

    @SuppressLint("CheckResult")
    fun syncAnswers() {
        lateinit var answers: List<AnsweredQuestionPOJO>
        db.formDetailsDao().getNotSyncedQuestions()
            .toObservable()
            .subscribeOn(Schedulers.io()).flatMap {
                answers = it
                syncAnswers(it)
            }.observeOn(AndroidSchedulers.mainThread()).subscribe({ response ->
                if (response.isCompletedSuccessfully) {
                    answers.forEach { item -> item.answeredQuestion.synced = true }
                    Observable.create<Unit> {
                        db.formDetailsDao()
                            .updateAnsweredQuestion(*answers.map { it.answeredQuestion }.toTypedArray())
                    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
                }
            }, {
                Log.i(TAG, it.message ?: "Error on synchronizing data")
            })
    }

    fun getNotSyncedQuestions(): LiveData<Int> = db.formDetailsDao().getCountOfNotSyncedQuestions()

    fun getNotes(
        countyCode: String,
        branchNumber: Int,
        selectedQuestion: Question?
    ): LiveData<List<Note>> {
        return if (selectedQuestion == null) {
            db.noteDao().getNotes(countyCode, branchNumber)
        } else {
            db.noteDao().getNotesForQuestion(countyCode, branchNumber, selectedQuestion.id)
        }
    }

    fun getNotSyncedNotes(): LiveData<Int> = db.noteDao().getCountOfNotSyncedNotes()

    fun saveNote(note: Note): Observable<ResponseBody> =
        Single.fromCallable { db.noteDao().save(note) }.toObservable().flatMap {
            note.id = it[0].toInt()
            postNote(note)
        }

    private fun postNote(note: Note): Observable<ResponseBody> {
        var body: MultipartBody.Part? = null
        var questionId = 0
        note.uriPath?.let {
            val file = File(it)
            val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        }
        note.questionId?.let {
            questionId = 0
        }


        return apiInterface.postNote(
            body, note.countyCode.createMultipart("CodJudet"),
            note.branchNumber.toString().createMultipart("NumarSectie"),
            questionId.toString().createMultipart("IdIntrebare"),
            note.description.createMultipart("TextNota")
        ).doOnNext {
            note.synced = true
            db.noteDao().updateNote(note)
        }
    }

    @SuppressLint("CheckResult")
    fun syncNotes() {
        db.noteDao().getNotSyncedNotes()
            .flatMap { Observable.fromIterable(it) }.flatMap {
                postNote(it)
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }, {
                Log.i(TAG, it.localizedMessage ?: "")
            })
    }

    private fun syncBranchDetails() {
        db.branchDetailsDao().getNotSyncedBranches().flatMap { Observable.fromIterable(it) }
            .flatMap {
                postBranchDetails(it)
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }, {
                Log.i(TAG, it.localizedMessage ?: "")
            })
    }

    fun syncData() {
        syncAnswers()
        syncNotes()
        syncBranchDetails()
    }


}
