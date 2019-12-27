package ro.code4.monitorizarevot.ui.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.android.inject
import ro.code4.monitorizarevot.analytics.Event
import ro.code4.monitorizarevot.analytics.Param
import ro.code4.monitorizarevot.analytics.ParamKey
import ro.code4.monitorizarevot.helper.logD
import ro.code4.monitorizarevot.helper.logW
import ro.code4.monitorizarevot.interfaces.AnalyticsScreenName

abstract class BaseAnalyticsFragment : Fragment(), AnalyticsScreenName {

    private val firebaseAnalytics: FirebaseAnalytics by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logAnalyticsEvent(
            Event.SCREEN_OPEN,
            Param(ParamKey.NAME, this.javaClass.simpleName))
    }

    override fun onResume() {
        super.onResume()

        firebaseAnalytics.setCurrentScreen(activity!!, getString(screenName), null)
    }

    fun logAnalyticsEvent(event: Event, vararg params: Param) {
        logD("logAnalyticsEvent: ${event.name}")
        val bundle = Bundle()
        for ((k, v) in params ) {
            when (v) {
                is String -> bundle.putString(k.name, v)
                is Int -> bundle.putInt(k.name, v)
                else -> logW("Not implemented bundle params for ${v.javaClass}")
            }
        }
        firebaseAnalytics.logEvent(event.name, bundle)
    }
}