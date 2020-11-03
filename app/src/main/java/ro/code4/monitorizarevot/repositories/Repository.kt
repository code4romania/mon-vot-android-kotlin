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
import ro.code4.monitorizarevot.data.model.response.VersionResponse
import ro.code4.monitorizarevot.data.pojo.AnsweredQuestionPOJO
import ro.code4.monitorizarevot.data.pojo.FormWithSections
import ro.code4.monitorizarevot.data.pojo.PollingStationInfo
import ro.code4.monitorizarevot.data.pojo.SectionWithQuestions
import ro.code4.monitorizarevot.helper.createMultipart
import ro.code4.monitorizarevot.services.ApiInterface
import ro.code4.monitorizarevot.services.LoginInterface
import java.io.File
import java.util.*


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

    private var syncInProgress = false
    fun login(user: User): Observable<LoginResponse> = loginInterface.login(user)

    fun getCounties(): Single<List<County>> {
        val observableApi = apiInterface.getCounties()
        val observableDb = db.countyDao().getAll().take(1).single(emptyList())
        return Single.zip(
            observableDb,
            observableApi.onErrorReturnItem(emptyList()),
            BiFunction<List<County>, List<County>, List<County>> { dbCounties, apiCounties ->
                val areAllApiCountiesInDb = apiCounties.all(dbCounties::contains)
                apiCounties.forEach {
                    it.name = it.name.toLowerCase(Locale.getDefault()).capitalize()
                }
                return@BiFunction when {
                    apiCounties.isNotEmpty() && !areAllApiCountiesInDb -> {
                        db.countyDao().deleteAll()
                        db.countyDao().save(*apiCounties.map { it }.toTypedArray())
                        apiCounties
                    }
                    apiCounties.isNotEmpty() && areAllApiCountiesInDb -> apiCounties
                    else -> dbCounties
                }
            }
        )
    }

    fun getPollingStationDetails(
        countyCode: String,
        pollingStationNumber: Int
    ): Observable<PollingStation> {
        return db.pollingStationDao().get(countyCode, pollingStationNumber).toObservable()
    }

    fun getPollingStationInfo(
        countyCode: String,
        pollingStationNumber: Int
    ): Observable<PollingStationInfo> {
        return db.pollingStationDao().getPollingStationInfo(countyCode, pollingStationNumber)
            .toObservable()
    }

    @SuppressLint("CheckResult")
    fun savePollingStationDetails(pollingStation: PollingStation) {
        Single.fromCallable { db.pollingStationDao().save(pollingStation) }.toObservable().flatMap {
            postPollingStationDetails(pollingStation)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({}, {
                Log.i(TAG, it.message.orEmpty())
            })
    }

    private fun postPollingStationDetails(pollingStation: PollingStation): Observable<ResponseBody> =
        apiInterface.postPollingStationDetails(pollingStation).doOnNext {
            pollingStation.synced = true
            db.pollingStationDao().updatePollingStationDetails(pollingStation)
        }

    fun getNotSyncedPollingStationsCount(): LiveData<Int> =
        db.pollingStationDao().getCountOfNotSyncedPollingStations()

    fun getAnswers(
        countyCode: String,
        pollingStationNumber: Int
    ): Observable<List<AnsweredQuestionPOJO>> =
        db.formDetailsDao().getAnswersFor(countyCode, pollingStationNumber)

    fun getFormsWithQuestions(): Observable<List<FormWithSections>> =
        db.formDetailsDao().getFormsWithSections()

    fun getSectionsWithQuestions(formId: Int): Observable<List<SectionWithQuestions>> =
        db.formDetailsDao().getSectionsWithQuestions(formId)

    fun getForms(): Observable<Unit> {
        val observableDb = db.formDetailsDao().getFormsWithSections()
        val observableApi = apiInterface.getForms()
        return Observable.zip(
            observableDb.onErrorReturn { null },
            observableApi.onErrorReturn { null },
            BiFunction<List<FormWithSections>?, VersionResponse?, Unit> { dbFormDetails, response ->
                processFormDetailsData(dbFormDetails, response)
            })
    }

    @SuppressLint("CheckResult")
    private fun deleteFormDetails(vararg formDetails: FormDetails) {
        db.formDetailsDao().deleteForms(*formDetails)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({}, {
                Log.i(TAG, it.message.orEmpty())
            })
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
        dbFormDetails: List<FormWithSections>?,
        response: VersionResponse?
    ) {
        if (response == null) {
            return
        }
        val apiFormDetails = response.formVersions
        if (dbFormDetails == null || dbFormDetails.isEmpty()) {
            saveFormDetails(apiFormDetails)
            return
        }
        if (apiFormDetails.size < dbFormDetails.size) {
            dbFormDetails.map { it.form }.minus(apiFormDetails).also { diff ->
                if (diff.isNotEmpty()) {
                    deleteFormDetails(*diff.map { it }.toTypedArray())
                }
            }
        }
        apiFormDetails.forEach { apiForm ->
            val dbForm = dbFormDetails.find { it.form.id == apiForm.id }
            if (dbForm != null && (apiForm.formVersion != dbForm.form.formVersion ||
                        apiForm.order != dbForm.form.order)) {
                deleteFormDetails(dbForm.form)
                saveFormDetails(apiForm)
            }
            if (dbForm == null) {
                saveFormDetails(apiForm)
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun getFormQuestions(form: FormDetails) {
        apiInterface.getForm(form.id).doOnNext { list ->
            list.forEach { section ->
                section.formId = form.id
                section.questions.forEach { question ->
                    question.sectionId = section.uniqueId
                    question.optionsToQuestions.forEach { answer ->
                        answer.questionId = question.id
                    }
                }
            }
            db.formDetailsDao().save(*list.map { it }.toTypedArray())
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({}, {
                Log.i(TAG, it.message.orEmpty())
            })

    }

    fun getAnswersForForm(
        countyCode: String?,
        pollingStationNumber: Int,
        formId: Int
    ): Observable<List<AnsweredQuestionPOJO>> {
        return db.formDetailsDao().getAnswersForForm(countyCode, pollingStationNumber, formId)
    }

    @SuppressLint("CheckResult")
    fun saveAnsweredQuestion(answeredQuestion: AnsweredQuestion, answers: List<SelectedAnswer>) {
        Observable.create<Unit> {
            db.formDetailsDao().insertAnsweredQuestion(answeredQuestion, answers)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({}, {
                Log.i(TAG, it.message.orEmpty())
            })
    }

    @SuppressLint("CheckResult")
    fun deleteAnsweredQuestion(answeredQuestion: AnsweredQuestion) {
        db.formDetailsDao().deleteAnsweredQuestion(answeredQuestion.id).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({}, {
                Log.i(TAG, it.message.orEmpty())
            })
    }


    @SuppressLint("CheckResult")
    fun syncAnswers(countyCode: String, pollingStationNumber: Int, formId: Int) {
        db.formDetailsDao().getNotSyncedQuestionsForForm(countyCode, pollingStationNumber, formId)
            .toObservable()
            .subscribeOn(Schedulers.io()).flatMap {
                syncAnswers(it)
            }.observeOn(AndroidSchedulers.mainThread()).subscribe({
                Observable.create<Unit> {
                    db.formDetailsDao()
                        .updateAnsweredQuestions(countyCode, pollingStationNumber, formId)
                }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            }, {
                Log.i(TAG, it.message ?: "Error on synchronizing data")
            })
    }

    private fun syncAnswers(list: List<AnsweredQuestionPOJO>): Observable<ResponseBody> {
        val responseAnswerContainer = ResponseAnswerContainer()
        responseAnswerContainer.answers = list.map {
            it.answeredQuestion.options = it.selectedAnswers
            return@map it.answeredQuestion
        }
        return apiInterface.postQuestionAnswer(responseAnswerContainer)
    }

    fun getNotSyncedQuestions(): LiveData<Int> = db.formDetailsDao().getCountOfNotSyncedQuestions()

    fun getNotes(
        countyCode: String,
        pollingStationNumber: Int,
        selectedQuestion: Question?
    ): LiveData<List<Note>> {
        return if (selectedQuestion == null) {
            db.noteDao().getNotes(countyCode, pollingStationNumber)
        } else {
            db.noteDao().getNotesForQuestion(countyCode, pollingStationNumber, selectedQuestion.id)
        }
    }

    fun getNotSyncedNotes(): LiveData<Int> = db.noteDao().getCountOfNotSyncedNotes()

    @SuppressLint("CheckResult")
    fun updateQuestionWithNotes(questionId: Int) {
        Observable.create<Unit> {
            db.formDetailsDao().updateQuestionWithNotes(questionId)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {
                Log.i(TAG, it.message.orEmpty())
            })
    }

    fun saveNote(note: Note): Observable<ResponseBody> =
        Single.fromCallable {
            db.noteDao().save(note).first()
        }.flatMapObservable {
            note.id = it.toInt()
            postNote(note)
        }

    private fun postNote(note: Note): Observable<ResponseBody> {
        val noteFile = note.uriPath?.let { File(it) }
        val body: MultipartBody.Part? = noteFile?.let {
            MultipartBody.Part.createFormData(
                "file",
                it.name,
                it.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            )
        }
        val questionId = note.questionId ?: 0

        return apiInterface.postNote(
            body,
            note.countyCode.createMultipart("CountyCode"),
            note.pollingStationNumber.toString().createMultipart("PollingStationNumber"),
            questionId.toString().createMultipart("QuestionId"),
            note.description.createMultipart("Text")
        ).doOnNext {
            note.synced = true
            db.noteDao().updateNote(note)
            noteFile?.delete()
        }
    }

    @SuppressLint("CheckResult")
    fun syncData() {
        if (!syncInProgress) {
            syncInProgress = true
            Observable.merge(
                mutableListOf(
                    syncAnswersObservable(),
                    syncPollingStationObservable(),
                    syncNotesObservable()
                )
            ).observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate {
                    syncInProgress = false
                }
                .subscribe({
                }, {
                    Log.i(TAG, it.localizedMessage.orEmpty())
                })
        }
    }

    @SuppressLint("CheckResult")
    fun syncAnswersObservable(): Observable<Unit> {
        lateinit var answers: List<AnsweredQuestionPOJO>
        return db.formDetailsDao().getNotSyncedQuestions()
            .toObservable()
            .flatMap {
                answers = it
                syncAnswers(it)
            }
            .flatMap {
                answers.forEach { item -> item.answeredQuestion.synced = true }
                Observable.create<Unit> { emitter ->
                    db.formDetailsDao()
                        .updateAnsweredQuestion(*answers.map { it.answeredQuestion }.toTypedArray())
                    emitter.onComplete()

                }
            }
            .subscribeOn(Schedulers.io())
    }

    @SuppressLint("CheckResult")
    private fun syncNotesObservable(): Observable<ResponseBody> {

        return db.noteDao().getNotSyncedNotes()
            .toObservable()
            .flatMap { Observable.fromIterable(it) }
            .flatMap { postNote(it) }
            .subscribeOn(Schedulers.io())
    }

    @SuppressLint("CheckResult")
    private fun syncPollingStationObservable(): Observable<ResponseBody> {
        return db.pollingStationDao().getNotSyncedPollingStations()
            .toObservable()
            .flatMap { Observable.fromIterable(it) }
            .flatMap { postPollingStationDetails(it) }
            .subscribeOn(Schedulers.io())
    }
}

