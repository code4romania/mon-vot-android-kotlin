package ro.code4.monitorizarevot.services

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import ro.code4.monitorizarevot.data.model.User
import ro.code4.monitorizarevot.data.model.response.LoginResponse

interface LoginInterface {
    @POST("access/authorize")
    fun login(@Body user: User): Observable<LoginResponse>
}