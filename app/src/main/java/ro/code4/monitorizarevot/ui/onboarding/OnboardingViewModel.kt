package ro.code4.monitorizarevot.ui.onboarding

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.koin.core.inject
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.helper.OnboardingChooseLanguageScreen
import ro.code4.monitorizarevot.adapters.helper.OnboardingScreen
import ro.code4.monitorizarevot.adapters.helper.OnboardingTutorialScreen
import ro.code4.monitorizarevot.helper.completedOnboarding
import ro.code4.monitorizarevot.ui.base.BaseViewModel
import java.util.*
import kotlin.collections.ArrayList

class OnboardingViewModel : BaseViewModel() {
    private val app: Application by inject()
    private val preferences: SharedPreferences by inject()
    private val onboardingLiveData = MutableLiveData<ArrayList<OnboardingScreen>>().apply {
        val screens = ArrayList<OnboardingScreen>()
        app.resources.getStringArray(R.array.languages)
        screens.add(
            OnboardingChooseLanguageScreen(
                languages = getLocales(
                    app.resources.getStringArray(
                        R.array.languages
                    )
                )
            )
        )
        screens.add(
            OnboardingTutorialScreen(
                R.drawable.ic_onboarding_1,
                R.string.onboarding_title_1,
                R.string.onboarding_description_1
            )
        )
        screens.add(
            OnboardingTutorialScreen(
                R.drawable.ic_onboarding_2,
                R.string.onboarding_title_2,
                R.string.onboarding_description_2
            )
        )
        screens.add(
            OnboardingTutorialScreen(
                R.drawable.ic_onboarding_3,
                R.string.onboarding_title_3,
                R.string.onboarding_description_3
            )
        )
        postValue(screens)
    }

    private fun getLocales(codes: Array<String>): List<Locale> = codes.map { Locale(it) }

    fun onboarding(): LiveData<ArrayList<OnboardingScreen>> = onboardingLiveData
    fun onboardingCompleted() {
        preferences.completedOnboarding()
    }
}