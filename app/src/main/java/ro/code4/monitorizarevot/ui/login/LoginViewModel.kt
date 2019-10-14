package ro.code4.monitorizarevot.ui.login

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.monitorizarevot.data.model.User
import ro.code4.monitorizarevot.data.model.response.LoginResponse
import ro.code4.monitorizarevot.helper.SingleLiveEvent
import ro.code4.monitorizarevot.helper.hasCompletedOnboarding
import ro.code4.monitorizarevot.helper.saveToken
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class LoginViewModel : BaseViewModel() {

    private val loginRepository: Repository by inject()
    private val sharedPreferences: SharedPreferences by inject()

    private val loginLiveData = SingleLiveEvent<Void>()
    private val onboardingLiveData = SingleLiveEvent<Void>()

    fun loggedIn(): LiveData<Void> = loginLiveData
    fun onboarding(): LiveData<Void> = onboardingLiveData
    private val disposable = CompositeDisposable()

    fun login(user: User) {
        disposable.add(
            loginRepository.login(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccessfulLogin, this::onError)
        )
    }

    override fun onCleared() {
        disposable.clear()
    }

    private fun onSuccessfulLogin(loginResponse: LoginResponse) {
        sharedPreferences.saveToken(loginResponse.accessToken)
        if (sharedPreferences.hasCompletedOnboarding()) {
            loginLiveData.call()
        } else {
            onboardingLiveData.call()
        }
    }
}
