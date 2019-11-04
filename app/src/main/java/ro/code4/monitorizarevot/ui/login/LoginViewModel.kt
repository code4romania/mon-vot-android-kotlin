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

    fun login(phone: String, password: String) {
        loginLiveData.postValue(Result.Loading)
        getFirebaseToken(phone, password)
    }

    private fun onSuccessfulLogin(loginResponse: LoginResponse, firebaseToken: String) {
        sharedPreferences.saveToken(loginResponse.accessToken)
        registerForNotification(firebaseToken)
    }

    private fun onSuccessfulRegisteredForNotification() {
        if (sharedPreferences.hasCompletedOnboarding()) {
            loginLiveData.postValue(Result.Success(PollingStationActivity::class.java))
        } else {
            loginLiveData.postValue(Result.Success(OnboardingActivity::class.java))
        }
    }

    private fun getFirebaseToken(phone: String, password: String) {
        try {


            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
                val firebaseToken = it.result?.token
                if (it.isSuccessful && firebaseToken != null) {
                    login(phone, password, firebaseToken)
                }
            }
        } catch (exception: IllegalStateException) {
            //The google services are not active - just for development purposes
            login(phone, password, "1234")
        }
    }

    fun login(phone: String, password: String, firebaseToken: String) {
        disposables.add(
            loginRepository.login(User(phone, password, firebaseToken))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ loginResponse ->
                    onSuccessfulLogin(loginResponse, firebaseToken)
                }, this::onError)
        )
    }

    private fun registerForNotification(firebaseToken: String) {
        disposables.add(
            loginRepository.registerForNotification(firebaseToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onSuccessfulRegisteredForNotification() }, this::onError)
        )
    }

    override fun onError(throwable: Throwable) {
        loginLiveData.postValue(Result.Failure(throwable))
    }
}
