package ro.code4.monitorizarevot.ui.splashscreen

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import org.koin.core.inject
import ro.code4.monitorizarevot.helper.getToken
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class SplashScreenViewModel: BaseViewModel() {
    val tokenLiveData: MutableLiveData<String?> by lazy {
        MutableLiveData<String?>()
    }

    private val sharedPreferences: SharedPreferences by inject()

    init {
        tokenLiveData.value = sharedPreferences.getToken()
    }

}