package ro.code4.monitorizarevot.services

import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import ro.code4.monitorizarevot.data.model.BranchDetails
import ro.code4.monitorizarevot.data.model.County
import ro.code4.monitorizarevot.data.model.ResponseAnswerContainer
import ro.code4.monitorizarevot.data.model.Section
import ro.code4.monitorizarevot.data.model.response.SyncResponse
import ro.code4.monitorizarevot.data.model.response.VersionResponse

//TODO to be translated
interface ApiInterface {
    @GET("/api/v1/formular")
    fun getForms(): Observable<VersionResponse>

    @GET("/api/v1/sectie")
    fun getCounties(): Single<List<County>>


    @GET("/api/v1/formular/{formCode}")
    fun getForm(@Path("formCode") formId: String): Observable<List<Section>>

    @POST("/api/v1/sectie")
    fun postBranchDetails(@Body branchDetails: BranchDetails): Call<ResponseBody>

    @POST("/api/v1/raspuns")
    fun postQuestionAnswer(@Body responseAnswer: ResponseAnswerContainer): Observable<SyncResponse>


    @Multipart
    @POST("/api/v1/note/ataseaza")
    fun postNote(
        @Part file: MultipartBody.Part?,
        @Part countyCode: MultipartBody.Part,
        @Part branchNumber: MultipartBody.Part,
        @Part questionId: MultipartBody.Part,
        @Part description: MultipartBody.Part
    ): Call<ResponseBody>

}