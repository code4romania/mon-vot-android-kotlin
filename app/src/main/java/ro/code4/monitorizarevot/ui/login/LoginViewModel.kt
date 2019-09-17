package ro.code4.monitorizarevot.ui.login

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins.onError
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.monitorizarevot.data.model.User
import ro.code4.monitorizarevot.data.model.response.LoginResponse
import ro.code4.monitorizarevot.helper.putToken
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class LoginViewModel : BaseViewModel() {

    private val loginRepository: Repository by inject()
    private val sharedPreferences: SharedPreferences by inject()

    private val loginLiveData = MutableLiveData<Boolean>()

    fun loggedIn(): LiveData<Boolean> = loginLiveData

    fun login(user: User) {
        loginRepository.login(user).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                onSuccessfulLogin(it)
            }, {
                onError(it)

            })
    }

    //TODO should check if token still available

    private fun onSuccessfulLogin(loginResponse: LoginResponse) {
        sharedPreferences.putToken(loginResponse.accessToken)
        loginLiveData.postValue(true)
    }
}
