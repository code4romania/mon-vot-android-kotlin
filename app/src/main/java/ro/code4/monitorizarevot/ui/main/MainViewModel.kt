package ro.code4.monitorizarevot.ui.main

import android.content.SharedPreferences
import org.koin.core.inject
import ro.code4.monitorizarevot.helper.deleteToken
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class MainViewModel : BaseViewModel() {
    private val sharedPreferences: SharedPreferences by inject()

    fun logout() {
        sharedPreferences.deleteToken()
    }
}