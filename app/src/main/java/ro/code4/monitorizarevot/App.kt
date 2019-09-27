package ro.code4.monitorizarevot

import android.app.Application
import com.sylversky.fontreplacer.FontReplacer
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ro.code4.monitorizarevot.modules.apiModule
import ro.code4.monitorizarevot.modules.appModule
import ro.code4.monitorizarevot.modules.dbModule
import ro.code4.monitorizarevot.modules.viewModelsModule

class App : Application() {

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(appModule, apiModule, dbModule, viewModelsModule))
        }
    }

    private fun replaceFonts() {
        val replacer = FontReplacer.Build(applicationContext)
        replacer.setDefaultFont("fonts/SourceSansPro-Regular.ttf")
        replacer.setBoldFont("fonts/SourceSansPro-Bold.ttf")
        replacer.setLightFont("fonts/SourceSansPro-Light.ttf")
        replacer.setMediumFont("fonts/SourceSansPro-SemiBold.ttf")
        replacer.applyFont()
    }
}