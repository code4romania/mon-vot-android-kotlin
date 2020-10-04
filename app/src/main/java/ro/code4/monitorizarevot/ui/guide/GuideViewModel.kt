package ro.code4.monitorizarevot.ui.guide

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import ro.code4.monitorizarevot.BuildConfig
import ro.code4.monitorizarevot.helper.Constants
import ro.code4.monitorizarevot.helper.getStringOrDefault
import ro.code4.monitorizarevot.ui.base.BaseViewModel
import java.net.URLEncoder

class GuideViewModel : BaseViewModel() {
    private val remoteConfig = runCatching { FirebaseRemoteConfig.getInstance() }.getOrNull()
    private val guideUrl by lazy {
        URLEncoder.encode(
            remoteConfig.getStringOrDefault(
                Constants.REMOTE_CONFIG_OBSERVER_GUIDE_URL,
                BuildConfig.GUIDE_URL
            ),
            "UTF-8"
        )
    }
    private val urlLiveData = MutableLiveData<String>(
        "https://docs.google.com/gview?embedded=true&url=$guideUrl"
    )

    fun url(): LiveData<String> = urlLiveData
}