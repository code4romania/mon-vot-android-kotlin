package ro.code4.monitorizarevot.ui.main

import android.content.SharedPreferences
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.koin.core.inject
import ro.code4.monitorizarevot.BuildConfig
import ro.code4.monitorizarevot.helper.Constants
import ro.code4.monitorizarevot.helper.SingleLiveEvent
import ro.code4.monitorizarevot.helper.completedPollingStationConfig
import ro.code4.monitorizarevot.helper.deleteToken
import ro.code4.monitorizarevot.helper.getStringOrDefault
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class MainViewModel : BaseViewModel() {
    private val sharedPreferences: SharedPreferences by inject()
    private val remoteConfig = runCatching { FirebaseRemoteConfig.getInstance() }.getOrNull()
    internal val safetyUrl by lazy {
        remoteConfig.getStringOrDefault(
            Constants.REMOTE_CONFIG_SAFETY_GUIDE_URL,
            BuildConfig.SAFETY_URL
        )
    }
    internal val observerFeedbackUrl by lazy {
        remoteConfig.getStringOrDefault(
            Constants.REMOTE_CONFIG_OBSERVER_FEEDBACK_URL,
            BuildConfig.OBSERVER_FEEDBACK_URL
        )
    }
    internal val isSafetyItemVisible = safetyUrl.isNotBlank()
    internal val isObserverFeedbackItemVisible = observerFeedbackUrl.isNotBlank()
    private val onLogoutLiveData = SingleLiveEvent<Void>()

    fun onLogoutLiveData(): SingleLiveEvent<Void> = onLogoutLiveData

    fun logout() {
        sharedPreferences.deleteToken()
        onLogoutLiveData.call()
    }

    fun notifyChangeRequested() {
        sharedPreferences.completedPollingStationConfig(false)
    }
}