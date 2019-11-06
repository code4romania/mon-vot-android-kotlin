package ro.code4.monitorizarevot.helper

import android.content.Context
import android.content.res.Configuration
import androidx.preference.PreferenceManager
import java.util.*

object LocaleManager {

    @Suppress("DEPRECATION")
    fun wrapContext(context: Context): Context {

        val savedLocale =
            PreferenceManager.getDefaultSharedPreferences(context).getLocaleCode().getLocale()
        // as part of creating a new context that contains the new locale we also need to override the default locale.
        Locale.setDefault(savedLocale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(savedLocale)
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
        return context
    }
}