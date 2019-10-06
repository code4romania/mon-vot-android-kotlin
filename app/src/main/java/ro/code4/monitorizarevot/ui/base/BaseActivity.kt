package ro.code4.monitorizarevot.ui.base

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject
import ro.code4.monitorizarevot.helper.getLanguage
import ro.code4.monitorizarevot.helper.switchLanguage
import ro.code4.monitorizarevot.interfaces.Layout
import ro.code4.monitorizarevot.interfaces.ViewModelSetter
import java.util.*

abstract class BaseActivity<out T : BaseViewModel> : AppCompatActivity(), Layout,
    ViewModelSetter<T> {

    private val preferences: SharedPreferences by inject()

    override fun attachBaseContext(newBase: Context?) {
        val config = Configuration()
        config.setLocale(Locale(preferences.getLanguage()))
        super.attachBaseContext(newBase?.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
    }

    fun switchLanguage() {
        preferences.switchLanguage()
        finish()
        startActivity(intent)
    }
}