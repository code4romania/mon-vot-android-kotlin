package ro.code4.monitorizarevot.ui.settings

import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.ui.base.BaseAnalyticsActivity

class SettingsActivity : BaseAnalyticsActivity<SettingsViewModel>() {
    override val layout = R.layout.activity_settings

    override val viewModel: SettingsViewModel by viewModel()

    override val screenName = R.string.settings_title
}