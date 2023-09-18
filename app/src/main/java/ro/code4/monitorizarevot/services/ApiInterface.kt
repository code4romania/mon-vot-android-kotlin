package ro.code4.monitorizarevot.services

import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*
import ro.code4.monitorizarevot.data.model.*
import ro.code4.monitorizarevot.data.model.response.MunicipalityResponse
import ro.code4.monitorizarevot.data.model.response.VersionResponse

interface ApiInterface {
    @GET("/api/v1/form")
    fun getForms(@Query("diaspora") diaspora: Boolean? = null): Observable<VersionResponse>

    @GET("/api/v1/county")
    fun getCounties(): Single<List<County>>

    @GET("/api/v1/county/{countyCode}/municipalities")
    fun getMunicipalities(@Path("countyCode") countyCode: String): Single<List<Municipality>>

    @GET("/api/v1/municipalities/all")
    fun getAllMunicipalities(): Single<List<MunicipalityResponse>>

    @GET("/api/v1/form/{formId}")
    fun getForm(@Path("formId") formId: Int): Observable<List<Section>>

    @POST("/api/v1/polling-station")
    fun postPollingStationDetails(@Body pollingStation: PollingStation): Observable<ResponseBody>

    @POST("/api/v1/answers")
    fun postQuestionAnswer(@Body responseAnswer: ResponseAnswerContainer): Observable<ResponseBody>

    @Multipart
    @POST("/api/v2/note")
    fun postNote(
        @Part files: Array<MultipartBody.Part>?,
        @Part countyCode: MultipartBody.Part,
        @Part municipalityCode: MultipartBody.Part,
        @Part pollingStationNumber: MultipartBody.Part,
        @Part questionId: MultipartBody.Part,
        @Part description: MultipartBody.Part
    ): Observable<ResponseBody>
}