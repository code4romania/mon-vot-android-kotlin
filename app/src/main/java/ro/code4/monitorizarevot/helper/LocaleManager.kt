package ro.code4.monitorizarevot.helper

import android.content.Context
import android.content.res.Configuration
import androidx.preference.PreferenceManager
import java.util.*

object LocaleManager {

    fun wrapContext(context: Context): Context {

        val savedLocale =
            Locale(PreferenceManager.getDefaultSharedPreferences(context).getLanguage())


        // as part of creating a new context that contains the new locale we also need to override the default locale.
        Locale.setDefault(savedLocale)

        // create new configuration with the saved locale
        val newConfig = Configuration()
        newConfig.setLocale(savedLocale)

        return context.createConfigurationContext(newConfig)
    }
}