package ro.code4.monitorizarevot.ui.login

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import org.koin.core.inject
import ro.code4.monitorizarevot.BuildConfig
import ro.code4.monitorizarevot.analytics.Event
import ro.code4.monitorizarevot.data.model.User
import ro.code4.monitorizarevot.data.model.response.LoginResponse
import ro.code4.monitorizarevot.helper.*
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel
import ro.code4.monitorizarevot.ui.onboarding.OnboardingActivity
import ro.code4.monitorizarevot.ui.section.PollingStationActivity

class LoginViewModel : BaseViewModel() {

    private val loginRepository: Repository by inject()
    private val sharedPreferences: SharedPreferences by inject()
    private val firebaseAnalytics: FirebaseAnalytics by inject()

    private val loginLiveData = SingleLiveEvent<Result<Class<*>>>()

    fun loggedIn(): LiveData<Result<Class<*>>> = loginLiveData

    fun login(phone: String, password: String) {
        loginLiveData.postValue(Result.Loading)
        getFirebaseToken(phone, password)
    }

    private fun onSuccessfulLogin(loginResponse: LoginResponse, firebaseToken: String) {
        logD("onSuccessfulLogin")
        sharedPreferences.saveToken(loginResponse.accessToken)
        registerForNotification(firebaseToken)
    }

    private fun onSuccessfulRegisteredForNotification() {
        logD("onSuccessfulRegisteredForNotification")
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
                } else {
                    logW("Failed to get firebase token!")
                    onError(Throwable())
                }
            }.addOnFailureListener(this::onError)
        } catch (exception: Exception) {
            //The google services are not active - just for development purposes
            if (BuildConfig.DEBUG && exception is IllegalStateException) {
                login(phone, password, "1234")
            } else {
                logW("Exception while trying to get firebase token!")
                onError(exception)
            }
        }
    }

    fun login(phone: String, password: String, firebaseToken: String) {
        logD("login: $phone : $password -> $firebaseToken")
        disposables.add(
            loginRepository.login(User(phone, password, firebaseToken))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ loginResponse ->
                    logD("Login successful! Token received!")
                    onSuccessfulLogin(loginResponse, firebaseToken)
                }, { throwable ->
                    firebaseAnalytics.logEvent(Event.LOGIN_FAILED.title, null)
                    logE("Login failed!", throwable)
                    onError(throwable)
                })
        )
    }

    private fun registerForNotification(firebaseToken: String) {
        logD("registerForNotification with $firebaseToken")
        disposables.add(
            loginRepository.registerForNotification(firebaseToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logD("Registered for notifications on firebase!")
                    onSuccessfulRegisteredForNotification()
                }, { throwable ->
                    logE("Register for notification failed!", throwable)
                    onError(throwable)
                })
        )
    }

    override fun onError(throwable: Throwable) {
        logE("onError ${throwable.message}", throwable)
        loginLiveData.postValue(Result.Failure(throwable))
    }
}
