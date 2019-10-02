package ro.code4.monitorizarevot.repositories

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Call
import retrofit2.Retrofit
import ro.code4.monitorizarevot.data.AppDatabase
import ro.code4.monitorizarevot.data.model.*
import ro.code4.monitorizarevot.data.model.answers.AnsweredQuestion
import ro.code4.monitorizarevot.data.model.answers.SelectedAnswer
import ro.code4.monitorizarevot.data.model.response.LoginResponse
import ro.code4.monitorizarevot.data.model.response.VersionResponse
import ro.code4.monitorizarevot.data.pojo.AnsweredQuestionPOJO
import ro.code4.monitorizarevot.data.pojo.FormWithSections
import ro.code4.monitorizarevot.data.pojo.SectionWithQuestions
import ro.code4.monitorizarevot.services.ApiInterface
import ro.code4.monitorizarevot.services.LoginInterface


class Repository : KoinComponent {

    private val retrofit: Retrofit by inject()
    private val db: AppDatabase by inject()
    private val loginInterface: LoginInterface by lazy {
        retrofit.create(LoginInterface::class.java)
    }
    private val apiInterface: ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }

    fun login(user: User): Observable<LoginResponse> = loginInterface.login(user)

    fun getCounties(): Observable<List<County>> {

        val observableApi = apiInterface.getCounties()
        val observableDb = db.countyDao().getAll().toObservable()

        return Observable.zip(
            observableDb.onErrorReturn { null },
            observableApi.onErrorReturn { null },
            BiFunction<List<County>?, List<County>?, List<County>> { dbCounties, apiCounties ->
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

    fun saveBranchDetails(branchDetails: BranchDetails) {
        db.branchDetailsDao().save(branchDetails).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe()
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

//    return apiFormDetails
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

    fun getForm(formId: String): Observable<List<Section>> = apiInterface.getForm(formId)

    fun getFormVersion(): Observable<VersionResponse> = apiInterface.getForms()

    fun postBranchDetails(branchDetails: BranchDetails): Call<ResponseBody> =
        apiInterface.postBranchDetails(branchDetails)


    fun postQuestionAnswer(responseAnswerContainer: ResponseAnswerContainer): Call<ResponseBody> =
        apiInterface.postQuestionAnswer(responseAnswerContainer)

    fun getAnswersForForm(
        countyCode: String?,
        branchNumber: Int,
        formCode: String
    ): LiveData<List<AnsweredQuestionPOJO>> {
        return db.formDetailsDao().getAnswersForForm(countyCode, branchNumber, formCode)
    }

    fun saveAnsweredQuestion(answeredQuestion: AnsweredQuestion, answers: List<SelectedAnswer>) {
        Observable.create<Any> {
            db.formDetailsDao().insertAnsweredQuestion(answeredQuestion, answers)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe()
    }


//    fun postNote(note: Note): Call<ResponseBody> {
//        var body: MultipartBody.Part? = null
//        var questionId = 0
//        note.uriPath?.let {
//            val file = File(it)
//
//            val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//            body = MultipartBody.Part.createFormData("file", file.name, requestFile)
//
//        }
//        note.questionId?.let {
//            questionId = 0
//        }
//
//
//        return apiInterface.postNote(
//            body, Preferences.getCountyCode().createMultipart("CodJudet"),
//            Preferences.getBranchNumber().createMultipart("NumarSectie"),
//            questionId.toString().createMultipart("IdIntrebare"),
//            note.description.createMultipart("TextNota")
//        )
//    }
}
