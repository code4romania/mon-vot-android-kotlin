package ro.code4.monitorizarevot.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.Constants
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
        progressDialog.show(supportFragmentManager, ProgressDialogFragment.TAG)
        viewModel.loginLiveData().observe(this, Observer { loginStatus ->
            progressDialog.dismiss()
            when {
                loginStatus.isLoggedIn && loginStatus.onboardingCompleted
                        && !loginStatus.isPollingStationConfigCompleted -> startActivityWithoutTrace(PollingStationActivity::class.java)
                loginStatus.isLoggedIn && loginStatus.isPollingStationConfigCompleted -> {
                    val notificationTitle = intent?.extras?.getString(Constants.PUSH_NOTIFICATION_TITLE)
                    val notificationBody = intent?.extras?.getString(Constants.PUSH_NOTIFICATION_BODY)
                    val intent = Intent(this, MainActivity::class.java)
                    notificationTitle?.let {
                        intent.putExtra(Constants.PUSH_NOTIFICATION_TITLE, it)
                    }
                    notificationBody?.let {
                        intent.putExtra(Constants.PUSH_NOTIFICATION_BODY, it)
                    }
                    startActivity(intent)
                    finishAffinity()
                }
                loginStatus.isLoggedIn -> startActivityWithoutTrace(OnboardingActivity::class.java)
                else -> startActivityWithoutTrace(LoginActivity::class.java)
            }
        })
    }

    override fun onDestroy() {
        if (progressDialog.isResumed) progressDialog.dismissAllowingStateLoss()
        super.onDestroy()
    }
}