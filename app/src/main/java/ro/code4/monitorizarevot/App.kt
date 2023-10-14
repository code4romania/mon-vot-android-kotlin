package ro.code4.monitorizarevot

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.sylversky.fontreplacer.FontReplacer
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ro.code4.monitorizarevot.helper.LocaleManager
import ro.code4.monitorizarevot.modules.*
import ro.code4.monitorizarevot.ui.base.BaseActivity

class App : Application() {
    var currentActivity: BaseActivity<*>? = null

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(appModule, apiModule, dbModule, viewModelsModule, analyticsModule))
        }
        replaceFonts()
        FirebaseApp.initializeApp(this);
    }

    private fun replaceFonts() {
        val replacer = FontReplacer.Build(applicationContext)
        replacer.setDefaultFont("fonts/SourceSansPro-Regular.ttf")
        replacer.setBoldFont("fonts/SourceSansPro-Bold.ttf")
        replacer.setLightFont("fonts/SourceSansPro-Light.ttf")
        replacer.setMediumFont("fonts/SourceSansPro-SemiBold.ttf")
        replacer.applyFont()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.wrapContext(base))
    }
}