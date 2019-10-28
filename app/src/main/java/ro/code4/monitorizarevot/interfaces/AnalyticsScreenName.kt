package ro.code4.monitorizarevot.interfaces

import androidx.annotation.StringRes

interface AnalyticsScreenName {
    @get:ExcludeFromCodeCoverage
    @get:StringRes
    val screenName: Int
}
