package ro.code4.monitorizarevot.ui.splashscreen

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import org.koin.core.inject
import ro.code4.monitorizarevot.BuildConfig
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.SingleLiveEvent
import ro.code4.monitorizarevot.helper.getToken
import ro.code4.monitorizarevot.helper.hasCompletedOnboarding
import ro.code4.monitorizarevot.helper.isPollingStationConfigCompleted
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class SplashScreenViewModel : BaseViewModel() {
    private val sharedPreferences: SharedPreferences by inject()
    private val loginLiveData = SingleLiveEvent<LoginStatus>()

    fun loginLiveData(): LiveData<LoginStatus> = loginLiveData

    init {
        remoteConfiguration()
    }

    private fun remoteConfiguration() {
        try {


            FirebaseRemoteConfig.getInstance().apply {
                val configSettings = FirebaseRemoteConfigSettings.Builder()
                    .build()
                setConfigSettingsAsync(configSettings)
                setDefaultsAsync(R.xml.remote_config_defaults)
                fetch(if (BuildConfig.DEBUG) 0 else 3600)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            FirebaseRemoteConfig.getInstance().activate()
                        }
                        checkLogin()
                    }

            }
        } catch (e: Exception) {
            checkLogin()
        }

    }

    private fun checkLogin() {
        val isLoggedIn = sharedPreferences.getToken() != null

        loginLiveData.postValue(
            LoginStatus(
                isLoggedIn,
                sharedPreferences.isPollingStationConfigCompleted(),
                sharedPreferences.hasCompletedOnboarding()
            )
        )
    }

    data class LoginStatus(
        val isLoggedIn: Boolean,
        val isPollingStationConfigCompleted: Boolean,
        val onboardingCompleted: Boolean
    )
}