package ro.code4.monitorizarevot.adapters.helper

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ro.code4.monitorizarevot.R
import java.util.*

sealed class OnboardingScreen(@DrawableRes val imageResId: Int, @StringRes val titleResId: Int)
class OnboardingTutorialScreen(
    imageResId: Int,
    titleResId: Int, @StringRes val descriptionResId: Int
) : OnboardingScreen(imageResId, titleResId)

class OnboardingChooseLanguageScreen(
    imageResId: Int = R.drawable.ic_choose_language,
    titleResId: Int = R.string.onboarding_title_choose_language,
    val languages: List<Locale>
) : OnboardingScreen(imageResId, titleResId)