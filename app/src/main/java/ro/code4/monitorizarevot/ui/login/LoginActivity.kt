package ro.code4.monitorizarevot.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings.Secure
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.BuildConfig
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.data.model.User
import ro.code4.monitorizarevot.helper.startActivityWithoutTrace
import ro.code4.monitorizarevot.ui.base.BaseActivity
import ro.code4.monitorizarevot.ui.branch.BranchActivity
import ro.code4.monitorizarevot.ui.onboarding.OnboardingActivity

class LoginActivity : BaseActivity<LoginViewModel>() {

    override val layout: Int
        get() = R.layout.activity_login
    override val viewModel: LoginViewModel by viewModel()

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginButton.setOnClickListener {
            val user = User(
                phone.text.toString(),
                password.text.toString(),
                Secure.getString(applicationContext.contentResolver, Secure.ANDROID_ID)
            )
            viewModel.login(user)
        }
        appVersion.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)
        viewModel.loggedIn().observe(this, Observer {
            startActivityWithoutTrace(BranchActivity::class.java)
        })
        viewModel.onboarding().observe(this, Observer {
            startActivityWithoutTrace(OnboardingActivity::class.java)
        })
    }

}
