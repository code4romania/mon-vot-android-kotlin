package ro.code4.monitorizarevot.services

import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import ro.code4.monitorizarevot.data.model.County
import ro.code4.monitorizarevot.data.model.PollingStation
import ro.code4.monitorizarevot.data.model.ResponseAnswerContainer
import ro.code4.monitorizarevot.data.model.Section
import ro.code4.monitorizarevot.data.model.response.VersionResponse

interface ApiInterface {
    @GET("/api/v1/form")
    fun getForms(@Query("diaspora") diaspora: Boolean? = null): Observable<VersionResponse>

    @GET("/api/v1/county")
    fun getCounties(): Single<List<County>>

    @GET("/api/v1/form/{formId}")
    fun getForm(@Path("formId") formId: Int): Observable<List<Section>>

    @POST("/api/v1/polling-station")
    fun postPollingStationDetails(@Body pollingStation: PollingStation): Observable<ResponseBody>

    @POST("/api/v1/answers")
    fun postQuestionAnswer(@Body responseAnswer: ResponseAnswerContainer): Observable<Response<Void>>

    @Multipart
    @POST("/api/v2/note/upload")
    fun postNote(
        @Part file: MultipartBody.Part?,
        @Part countyCode: MultipartBody.Part,
        @Part pollingStationNumber: MultipartBody.Part,
        @Part questionId: MultipartBody.Part,
        @Part description: MultipartBody.Part
    ): Observable<ResponseBody>

}