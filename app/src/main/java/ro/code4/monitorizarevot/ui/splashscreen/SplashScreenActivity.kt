package ro.code4.monitorizarevot.ui.splashscreen

import android.os.Bundle
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.startActivityWithoutTrace
import ro.code4.monitorizarevot.ui.base.BaseAnalyticsActivity
import ro.code4.monitorizarevot.ui.login.LoginActivity
import ro.code4.monitorizarevot.ui.main.MainActivity
import ro.code4.monitorizarevot.ui.onboarding.OnboardingActivity
import ro.code4.monitorizarevot.ui.section.PollingStationActivity
import ro.code4.monitorizarevot.widget.ProgressDialogFragment

class SplashScreenActivity : BaseAnalyticsActivity<SplashScreenViewModel>() {

    override val layout: Int
        get() = R.layout.activity_splash_screen
    override val screenName: Int
        get() = R.string.analytics_title_splash

    private val progressDialog: ProgressDialogFragment by lazy {
        ProgressDialogFragment().also {
            it.isCancelable = false
        }
    }
    override val viewModel: SplashScreenViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressDialog.showNow(supportFragmentManager, ProgressDialogFragment.TAG)
        viewModel.loginLiveData().observe(this, Observer { loginStatus ->
            progressDialog.dismiss()
            val activity: Class<*> = when {
                loginStatus.isLoggedIn && loginStatus.onboardingCompleted && !loginStatus.isPollingStationConfigCompleted -> PollingStationActivity::class.java
                loginStatus.isLoggedIn && loginStatus.isPollingStationConfigCompleted -> MainActivity::class.java
                loginStatus.isLoggedIn -> OnboardingActivity::class.java
                else -> LoginActivity::class.java
            }

            startActivityWithoutTrace(activity)
        })
    }

    override fun onDestroy() {
        if (progressDialog.isResumed) progressDialog.dismissAllowingStateLoss()
        super.onDestroy()
    }
}