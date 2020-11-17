package ro.code4.monitorizarevot.ui.splashscreen

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import org.koin.core.inject
import ro.code4.monitorizarevot.BuildConfig
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.*
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class SplashScreenViewModel : BaseViewModel() {
    private val sharedPreferences: SharedPreferences by inject()
    private val repository: Repository by inject()
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
                        checkResetDB()
                        checkLogin()
                    }

            }
        } catch (e: Exception) {
            checkResetDB()
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

    private fun checkResetDB() {
        val roundStartTimestamp = try {
            FirebaseRemoteConfig.getInstance().getLong(Constants.REMOTE_CONFIG_ROUND_START_TIMESTAMP)
        } catch (e: Exception) {
            0L
        }
        val lastDbReset = sharedPreferences.getLastDbResetTimestamp()

        val currentTimestamp = System.currentTimeMillis()/1000

        if (roundStartTimestamp in 1 until currentTimestamp && (lastDbReset == 0L || lastDbReset < roundStartTimestamp)) {
            sharedPreferences.clearUserPrefs()
            repository.clearDBData()

            sharedPreferences.setLastDbResetTimestamp(currentTimestamp)
        }
    }
}