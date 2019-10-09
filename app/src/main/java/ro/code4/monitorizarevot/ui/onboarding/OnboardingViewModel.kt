package ro.code4.monitorizarevot.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.helper.OnboardingScreen
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class OnboardingViewModel : BaseViewModel() {
    private val onboardingLiveData = MutableLiveData<ArrayList<OnboardingScreen>>().apply {
        val screens = ArrayList<OnboardingScreen>()
        screens.add(
            OnboardingScreen(
                R.string.onboarding_title_1,
                R.string.onboarding_description_1,
                R.drawable.ic_onboarding_1
            )
        )
        screens.add(
            OnboardingScreen(
                R.string.onboarding_title_2,
                R.string.onboarding_description_2,
                R.drawable.ic_onboarding_2
            )
        )
        screens.add(
            OnboardingScreen(
                R.string.onboarding_title_3,
                R.string.onboarding_description_3,
                R.drawable.ic_onboarding_3
            )
        )
        postValue(screens)
    }

    fun onboarding(): LiveData<ArrayList<OnboardingScreen>> = onboardingLiveData
}