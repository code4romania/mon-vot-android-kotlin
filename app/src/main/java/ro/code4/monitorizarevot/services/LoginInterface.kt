package ro.code4.monitorizarevot.services

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import ro.code4.monitorizarevot.data.model.User

interface LoginInterface {
    @POST("access/token")
    fun login(@Body user: User): Observable<User>
}