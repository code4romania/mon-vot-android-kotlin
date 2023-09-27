package ro.code4.monitorizarevot.helper

import android.content.Context
import java.util.*
import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import java.util.Locale

/**
 * Set application locale manually.
 *
 * Inspired with: https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758
 */
fun Application.setLocale(locale: Locale) {
    setLocaleInternal(locale)
    registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks(locale))
    registerComponentCallbacks(ApplicationCallbacks(this, locale))
}

private fun Context.setLocaleInternal(locale: Locale) {
    Locale.setDefault(locale)

    val resources = this.resources
    val currentLocale = resources.configuration.localeCompat
    if (currentLocale != locale) {
        val config = resources.configuration.apply {
            setLocale(locale)
        }
        // createConfigurationContext doesn't fit to our purpose
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}

@Suppress("DEPRECATION") // locale used for compatibility with old versions
private val Configuration.localeCompat: Locale
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) locales.get(0) else locale

private class ActivityLifecycleCallbacks(private val locale: Locale) : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activity.setLocaleInternal(locale)
    }

    // <editor-fold desc="Unused callbacks" defaultstate="collapsed">
    override fun onActivityStarted(activity: Activity) { /* do nothing */ }

    override fun onActivityResumed(activity: Activity) { /* do nothing */ }

    override fun onActivityPaused(activity: Activity) { /* do nothing */ }

    override fun onActivityStopped(activity: Activity) { /* do nothing */ }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) { /* do nothing */ }

    override fun onActivityDestroyed(activity: Activity) { /* do nothing */ }
    // </editor-fold>
}

private class ApplicationCallbacks(private val context: Context, private val locale: Locale) : ComponentCallbacks {

    override fun onConfigurationChanged(newConfig: Configuration) {
        context.setLocaleInternal(locale)
    }

    override fun onLowMemory() { /* do nothing */ }
}