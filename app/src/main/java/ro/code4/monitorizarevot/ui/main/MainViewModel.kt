package ro.code4.monitorizarevot.ui.main

import android.content.SharedPreferences
import org.koin.core.inject
import ro.code4.monitorizarevot.helper.SingleLiveEvent
import ro.code4.monitorizarevot.helper.deleteToken
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class MainViewModel : BaseViewModel() {
    private val sharedPreferences: SharedPreferences by inject()
    private val onLogoutLiveData = SingleLiveEvent<Void>()

    fun onLogoutLiveData(): SingleLiveEvent<Void> = onLogoutLiveData

    fun logout() {
        sharedPreferences.deleteToken()
        onLogoutLiveData.call()
    }
}