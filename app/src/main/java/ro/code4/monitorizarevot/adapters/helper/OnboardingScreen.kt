package ro.code4.monitorizarevot.adapters.helper

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingScreen(
    @StringRes val titleResId: Int,
    @StringRes val descriptionResId: Int,
    @DrawableRes val imageResId: Int
)