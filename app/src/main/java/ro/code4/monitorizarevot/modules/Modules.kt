package ro.code4.monitorizarevot.modules

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ro.code4.monitorizarevot.App
import ro.code4.monitorizarevot.BuildConfig.API_URL
import ro.code4.monitorizarevot.BuildConfig.DEBUG
import ro.code4.monitorizarevot.data.AppDatabase
import ro.code4.monitorizarevot.helper.getToken
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.section.VisitedPollingStationsViewModel
import ro.code4.monitorizarevot.ui.forms.FormsViewModel
import ro.code4.monitorizarevot.ui.forms.questions.QuestionsDetailsViewModel
import ro.code4.monitorizarevot.ui.forms.questions.QuestionsViewModel
import ro.code4.monitorizarevot.ui.guide.GuideViewModel
import ro.code4.monitorizarevot.ui.login.LoginViewModel
import ro.code4.monitorizarevot.ui.main.MainViewModel
import ro.code4.monitorizarevot.ui.notes.NoteViewModel
import ro.code4.monitorizarevot.ui.onboarding.OnboardingViewModel
import ro.code4.monitorizarevot.ui.section.PollingStationViewModel
import ro.code4.monitorizarevot.ui.section.selection.PollingStationSelectionViewModel
import ro.code4.monitorizarevot.ui.splashscreen.SplashScreenViewModel
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

val gson: Gson by lazy {
    val gsonBuilder = GsonBuilder()
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().create()
}

val appModule = module {
    single { App.instance }
}

val apiModule = module {
    single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(androidContext()) }
    single {
        Interceptor { chain ->
            val original = chain.request()

            val token = get<SharedPreferences>().getToken()
            val request = original.newBuilder()
                .header("Authorization", "Bearer $token")
                .header("Content-Type", "application/json")
                .build()

            chain.proceed(request)
        }
    }

    single {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level =
            if (DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        interceptor
    }

    single {
        val httpClient = OkHttpClient.Builder()
        httpClient.readTimeout(10, TimeUnit.SECONDS)
        httpClient.writeTimeout(10, TimeUnit.SECONDS)
        httpClient.connectTimeout(10, TimeUnit.SECONDS)
        get<Interceptor?>()?.let {
            httpClient.addInterceptor(it)
        }
        get<HttpLoggingInterceptor?>()?.let {
            httpClient.addInterceptor(it)
        }
        httpClient.build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(get<OkHttpClient>())
            .build()
    }
    single {
        Repository()
    }
}

val dbModule = module {
    single { AppDatabase.getDatabase(get()) }
    single { Executors.newSingleThreadExecutor() }
}

val viewModelsModule = module {
    viewModel { LoginViewModel() }
    viewModel { OnboardingViewModel() }
    viewModel { MainViewModel() }
    viewModel { PollingStationViewModel() }
    viewModel { PollingStationSelectionViewModel() }
    viewModel { VisitedPollingStationsViewModel(get()) }
    viewModel { FormsViewModel() }
    viewModel { QuestionsViewModel() }
    viewModel { QuestionsDetailsViewModel() }
    viewModel { NoteViewModel() }
    viewModel { GuideViewModel() }
    viewModel { SplashScreenViewModel() }
}

val analyticsModule = module {
    single { FirebaseAnalytics.getInstance(get()) }
}