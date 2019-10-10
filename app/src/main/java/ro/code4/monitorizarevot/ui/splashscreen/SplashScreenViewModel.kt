package ro.code4.monitorizarevot.ui.splashscreen

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import org.koin.core.inject
import ro.code4.monitorizarevot.helper.SingleLiveEvent
import ro.code4.monitorizarevot.helper.getToken
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class SplashScreenViewModel: BaseViewModel() {
    private val sharedPreferences: SharedPreferences by inject()
    private val loginLiveData = SingleLiveEvent<Boolean?>()

    fun loginLiveData(): LiveData<Boolean?> = loginLiveData

    fun checkLogin() {
        if (sharedPreferences.getToken() != null) {
            loginLiveData.postValue(true)
        } else {
            loginLiveData.postValue(false)
        }
    }
}