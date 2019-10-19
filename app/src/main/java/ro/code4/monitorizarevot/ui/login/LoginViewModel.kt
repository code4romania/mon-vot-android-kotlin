package ro.code4.monitorizarevot.ui.login

import android.app.Activity
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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
import ro.code4.monitorizarevot.ui.branch.BranchActivity
import ro.code4.monitorizarevot.ui.onboarding.OnboardingActivity

class LoginViewModel : BaseViewModel() {

    private val loginRepository: Repository by inject()
    private val sharedPreferences: SharedPreferences by inject()

    private val loginLiveData = SingleLiveEvent<Result<Class<*>>>()

    fun loggedIn(): LiveData<Result<Class<*>>> = loginLiveData
    private val disposable = CompositeDisposable()

    fun login(user: User) {
        loginLiveData.postValue(Result.Loading)
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

    //TODO should check if token still available
    private fun onSuccessfulLogin(loginResponse: LoginResponse) {
        sharedPreferences.saveToken(loginResponse.accessToken)
        if (sharedPreferences.hasCompletedOnboarding()) {
            loginLiveData.postValue(Result.Success(BranchActivity::class.java))
        } else {
            loginLiveData.postValue(Result.Success(OnboardingActivity::class.java))
        }
    }

    override fun onError(throwable: Throwable) {
        loginLiveData.postValue(Result.Failure(throwable))
    }
}
