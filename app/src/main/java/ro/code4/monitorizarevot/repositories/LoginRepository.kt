package ro.code4.monitorizarevot.repositories

import io.reactivex.Observable
import retrofit2.Retrofit
import ro.code4.monitorizarevot.data.model.User
import ro.code4.monitorizarevot.services.LoginInterface

class LoginRepository(private val retrofit: Retrofit) {

    private val loginInterface: LoginInterface by lazy {
        retrofit.create(LoginInterface::class.java)
    }

    fun login(user: User): Observable<User> = loginInterface.login(user)
}
