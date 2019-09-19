package ro.code4.monitorizarevot.repositories

import android.util.Log
import io.reactivex.Observable
import okhttp3.ResponseBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Call
import retrofit2.Retrofit
import ro.code4.monitorizarevot.data.AppDatabase
import ro.code4.monitorizarevot.data.model.*
import ro.code4.monitorizarevot.data.model.response.LoginResponse
import ro.code4.monitorizarevot.data.model.response.VersionResponse
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
        val observableApi = apiInterface.getCounties().doOnNext { list ->
            Log.i("GAGA", "SAVED TO DB")
            db.countyDao().save(*list.map { it }.toTypedArray())
        }
        val observableDb = db.countyDao().getAll().toObservable()

        return Observable.concatArrayEager(observableApi, observableDb)
    }

    fun getForm(formId: String): Observable<List<Section>> = apiInterface.getForm(formId)

    fun getFormVersion(): Observable<VersionResponse> = apiInterface.getFormVersion()

    fun postBranchDetails(branchDetails: BranchDetails): Call<ResponseBody> =
        apiInterface.postBranchDetails(branchDetails)


    fun postQuestionAnswer(responseAnswerContainer: ResponseAnswerContainer): Call<ResponseBody> =
        apiInterface.postQuestionAnswer(responseAnswerContainer)

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
