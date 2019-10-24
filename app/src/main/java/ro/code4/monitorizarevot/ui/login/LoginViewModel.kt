package ro.code4.monitorizarevot.ui.login

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.monitorizarevot.data.model.User
import ro.code4.monitorizarevot.data.model.response.LoginResponse
import ro.code4.monitorizarevot.helper.Result
import ro.code4.monitorizarevot.helper.SingleLiveEvent
import ro.code4.monitorizarevot.helper.hasCompletedOnboarding
import ro.code4.monitorizarevot.helper.saveToken
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel
import ro.code4.monitorizarevot.ui.onboarding.OnboardingActivity
import ro.code4.monitorizarevot.ui.section.PollingStationActivity

class LoginViewModel : BaseViewModel() {

    private val loginRepository: Repository by inject()
    private val sharedPreferences: SharedPreferences by inject()

    private val loginLiveData = SingleLiveEvent<Result<Class<*>>>()

    fun loggedIn(): LiveData<Result<Class<*>>> = loginLiveData

    fun login(user: User) {
        loginLiveData.postValue(Result.Loading)
        disposables.add(
            loginRepository.login(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccessfulLogin, this::onError)
        )
    }

    private fun onSuccessfulLogin(loginResponse: LoginResponse) {
        sharedPreferences.saveToken(loginResponse.accessToken)
        registerForNotification()
    }

    private fun onSuccessfulRegisterForNotification() {
        if (sharedPreferences.hasCompletedOnboarding()) {
            loginLiveData.postValue(Result.Success(PollingStationActivity::class.java))
        } else {
            loginLiveData.postValue(Result.Success(OnboardingActivity::class.java))
        }
    }

    private fun registerForNotification() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if (it.isSuccessful) {
                disposables.add(
                    loginRepository.registerForNotification(it.result?.token.orEmpty())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ onSuccessfulRegisterForNotification() }, this::onError)
                )
            }

        }
    }

    override fun onError(throwable: Throwable) {
        loginLiveData.postValue(Result.Failure(throwable))
    }
}
