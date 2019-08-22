package ro.code4.monitorizarevot.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins.onError
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import ro.code4.monitorizarevot.data.model.User
import ro.code4.monitorizarevot.repositories.LoginRepository

class LoginViewModel: ViewModel(), KoinComponent {

    private val loginRepository: LoginRepository by inject()

    private val loginLiveData = MutableLiveData<Boolean>()

    fun loggedIn(): LiveData<Boolean> = loginLiveData

    fun login(user: User) {
        loginRepository.login(user).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread())
            .subscribe({
                onSuccessfulLogin()
            }, {
                onError(it)

            })
    }

    private fun onSuccessfulLogin() {
        loginLiveData.postValue(true)
    }
}
