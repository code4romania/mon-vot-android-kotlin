package ro.code4.monitorizarevot.ui.main

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import org.koin.core.inject
import ro.code4.monitorizarevot.helper.SingleLiveEvent
import ro.code4.monitorizarevot.helper.deleteToken
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class MainViewModel : BaseViewModel() {
    private val sharedPreferences: SharedPreferences by inject()
    private val onLogoutLiveData = SingleLiveEvent<Boolean>()

    fun onLogoutLiveData(): LiveData<Boolean> = onLogoutLiveData

    fun logout() {
        sharedPreferences.deleteToken()
        onLogoutLiveData.postValue(true)
    }
}