package ro.code4.monitorizarevot.repositories

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
import ro.code4.monitorizarevot.data.model.response.ErrorVersionResponse
import ro.code4.monitorizarevot.data.model.response.LoginResponse
import ro.code4.monitorizarevot.data.model.response.PostNoteResponse
import ro.code4.monitorizarevot.data.model.response.VersionResponse
import ro.code4.monitorizarevot.data.pojo.*
import ro.code4.monitorizarevot.helper.Constants
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

    private val postTypeToken = object : TypeToken<PostNoteResponse>() {}.type

    private var syncInProgress = false
    fun login(user: User): Observable<LoginResponse> = loginInterface.login(user)

    fun getProvinces(): Single<List<Province>> {
        val observableApi = apiInterface.getProvinces()
        val observableDb = db.provinceDao().getAll().take(1).single(emptyList())
        return Single.zip(
            observableDb,
            observableApi.onErrorReturnItem(emptyList()),
            BiFunction<List<Province>, List<Province>, List<Province>> { dbProvinces, apiProvinces ->
                val areAllProvincesInDb = apiProvinces.all(dbProvinces::contains)
                return@BiFunction when {
                    apiProvinces.isNotEmpty() && !areAllProvincesInDb -> {
                        db.provinceDao().deleteAll()
                        db.provinceDao().save(*apiProvinces.map { it }.toTypedArray())
                        apiProvinces
                    }
                    apiProvinces.isNotEmpty() && areAllProvincesInDb -> apiProvinces
                    else -> dbProvinces
                }
            }
        )
    }

    fun getCounties(provinceCode: String): Single<List<County>> {
        var provinces = db.provinceDao().getAll().subscribe { p ->
            val a = 0
        }
        val observableApi = apiInterface.getCounties(provinceCode)
        val observableDb = db.countyDao().getByProvinceCode(provinceCode).take(1).single(emptyList())
        return Single.zip(
            observableDb,
            observableApi.onErrorReturnItem(emptyList()),
            BiFunction<List<County>, List<County>, List<County>> { dbCounties, apiCounties ->
                val areAllApiCountiesInDb = apiCounties.all(dbCounties::contains)
                return@BiFunction when {
                    apiCounties.isNotEmpty() && !areAllApiCountiesInDb -> {
                        db.countyDao().deleteAll(provinceCode)
                        db.countyDao().save(*apiCounties.map { it }.toTypedArray())
                        apiCounties
                    }
                    apiCounties.isNotEmpty() && areAllApiCountiesInDb -> apiCounties
                    else -> dbCounties
                }
            }
        )
    }

    fun getMunicipalities(countyCode: String): Single<List<Municipality>> {
        val observableApi = apiInterface.getMunicipalities(countyCode)
        val observableDb = db.municipalityDao().getByCounty(countyCode).take(1).single(emptyList())
        return Single.zip(
            observableDb,
            observableApi.onErrorReturnItem(emptyList()),
            BiFunction<List<Municipality>, List<Municipality>, List<Municipality>>() { dbMunicipalities, apiMunicipalities ->
                val areAllApiMunicipalitiesInDb = apiMunicipalities.all(dbMunicipalities::contains)

                return@BiFunction when {
                    apiMunicipalities.isNotEmpty() && !areAllApiMunicipalitiesInDb -> {
                        db.municipalityDao().deleteAll(countyCode)
                        db.municipalityDao().save(*apiMunicipalities.map { it }.toTypedArray())
                        apiMunicipalities
                    }
                    apiMunicipalities.isNotEmpty() && areAllApiMunicipalitiesInDb -> apiMunicipalities
                    else -> dbMunicipalities
                }
            }
        )
    }

    fun getPollingStationDetails(
        countyCode: String,
        municipalityCode: String,
        pollingStationNumber: Int
    ): Observable<PollingStation> {
        return db.pollingStationDao().get(countyCode, municipalityCode, pollingStationNumber).toObservable()
    }

    fun getPollingStationInfo(
        countyCode: String,
        municipalityCode: String,
        pollingStationNumber: Int
    ): Observable<PollingStationInfo> {
        return db.pollingStationDao().getPollingStationInfo(countyCode, municipalityCode, pollingStationNumber)
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
        municipalityCode: String,
        pollingStationNumber: Int
    ): Observable<List<AnsweredQuestionPOJO>> =
        db.formDetailsDao().getAnswersFor(countyCode, municipalityCode, pollingStationNumber)

    fun getFormsWithQuestions(): Observable<List<FormWithSections>> =
        db.formDetailsDao().getFormsWithSections()

    fun getSectionsWithQuestions(formId: Int): Observable<List<SectionWithQuestions>> =
        db.formDetailsDao().getSectionsWithQuestions(formId)

    fun getForms(): Observable<Unit> {
        val observableDb = db.formDetailsDao().getFormsWithSections()
        val observableApi = apiInterface.getForms()
        return Observable.zip(
            observableDb.onErrorReturn { listOf(ErrorFormWithSections(it)) },
            observableApi.onErrorReturn { ErrorVersionResponse(it) },
            BiFunction<List<FormWithSections>, VersionResponse, Unit> { dbFormDetails, response ->
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
        dbFormDetails: List<FormWithSections>,
        response: VersionResponse
    ) {
        if (response is ErrorVersionResponse) {
            return
        }
        val apiFormDetails = response.formVersions
        if ((dbFormDetails.size == 1 && dbFormDetails[0] is ErrorFormWithSections)
            || dbFormDetails.isEmpty()
        ) {
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
                        apiForm.order != dbForm.form.order)
            ) {
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
        municipalityCode: String,
        pollingStationNumber: Int,
        formId: Int
    ): Observable<List<AnsweredQuestionPOJO>> {
        return db.formDetailsDao().getAnswersForForm(countyCode, municipalityCode, pollingStationNumber, formId)
    }

    @SuppressLint("CheckResult")
    fun saveAnsweredQuestion(answeredQuestion: AnsweredQuestion, answers: List<SelectedAnswer>) {
        Observable.fromCallable<Boolean> {
            db.formDetailsDao().insertAnsweredQuestion(answeredQuestion, answers)
            true
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                Log.d(TAG, "Saving answered question: $answeredQuestion(answers: $answers)")
            }, {
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
    fun syncAnswers(countyCode: String, municipalityCode: String, pollingStationNumber: Int, formId: Int) {
        db.formDetailsDao().getNotSyncedQuestionsForForm(countyCode, municipalityCode, pollingStationNumber, formId)
            .toObservable()
            .subscribeOn(Schedulers.io()).flatMap(
                {
                    Log.d(TAG, "Syncing answers: ${it.size} answers to sync")
                    syncAnswers(it)
                },
                { answersList, response -> Pair(answersList, response) }
            ).observeOn(AndroidSchedulers.mainThread()).subscribe({
                if (it.first.isNotEmpty()) {
                    Observable.fromCallable {
                        db.formDetailsDao()
                            .updateAnsweredQuestions(countyCode, municipalityCode, pollingStationNumber, formId)
                    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
                }
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
        municipalityCode: String,
        pollingStationNumber: Int,
        selectedQuestion: Question?
    ): LiveData<List<Note>> {
        return if (selectedQuestion == null) {
            db.noteDao().getNotes(countyCode, municipalityCode, pollingStationNumber)
        } else {
            db.noteDao().getNotesForQuestion(countyCode, municipalityCode, pollingStationNumber, selectedQuestion.id)
        }
    }

    fun getNotesAsObservable(
        countyCode: String,
        municipalityCode: String,
        pollingStationNumber: Int,
        selectedQuestion: Question?
    ): Observable<List<Note>> {
        return if (selectedQuestion == null) {
            db.noteDao().getNotesAsObservable(countyCode, municipalityCode, pollingStationNumber)
        } else {
            db.noteDao().getNotesForQuestionAsObservable(countyCode, municipalityCode, pollingStationNumber, selectedQuestion.id)
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
        val noteFiles = note.uriPath?.let {
            if (it.isEmpty()) return@let null
            val filePaths = it.split(Constants.FILES_PATHS_SEPARATOR)
            if (filePaths.isEmpty()) null else filePaths.map { path -> File(path) }
        }?.filter { it.exists() }
        Log.d(TAG, "Files to be uploaded with note: ${noteFiles?.map { it.absolutePath }}")
        val body: Array<MultipartBody.Part>? = noteFiles?.let { paths ->
            mutableListOf<MultipartBody.Part>().apply {
                paths.forEach { file ->
                    this.add(
                        MultipartBody.Part.createFormData(
                            "files",
                            file.name,
                            file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                        )
                    )
                }
            }
        }?.toTypedArray()
        val questionId = note.questionId ?: 0

        return apiInterface.postNote(
            body,
            note.provinceCode.createMultipart("ProvinceCode"),
            note.countyCode.createMultipart("CountyCode"),
            note.municipalityCode.createMultipart("MunicipalityCode"),
            note.pollingStationNumber.toString().createMultipart("PollingStationNumber"),
            questionId.toString().createMultipart("QuestionId"),
            note.description.createMultipart("Text")
        ).doOnNext {
            note.synced = true
            note.uriPath = combineApiFilesUrls(it)
            db.noteDao().updateNote(note)
            noteFiles?.forEach { uploadedFile -> uploadedFile.delete() }
        }
    }

    private fun combineApiFilesUrls(response: ResponseBody): String? = kotlin.runCatching {
        val parsedResponse = Gson().fromJson<PostNoteResponse>(response.charStream(), postTypeToken)
        parsedResponse.filesAddress.joinToString(separator = Constants.FILES_PATHS_SEPARATOR)
    }.getOrNull()

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

    fun clearDBData() {
        AsyncTask.execute {
            db.noteDao().deleteAll()

            db.formDetailsDao().deleteAllAnswers()
            db.formDetailsDao().deleteAllQuestions()
            db.formDetailsDao().deleteAllSections()
            db.formDetailsDao().deleteAllForms()

            db.pollingStationDao().deleteAll()
        }
    }

    fun getVisitedStations() = db.pollingStationDao().getVisitedPollingStations()
}

