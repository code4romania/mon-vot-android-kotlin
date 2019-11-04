package ro.code4.monitorizarevot.ui.login

import android.os.Bundle
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.BuildConfig
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.startActivityWithoutTrace
import ro.code4.monitorizarevot.ui.base.BaseAnalyticsActivity
import ro.code4.monitorizarevot.widget.ProgressDialogFragment

class LoginActivity : BaseAnalyticsActivity<LoginViewModel>() {

    private val progressDialog: ProgressDialogFragment by lazy {
        ProgressDialogFragment().also {
            it.isCancelable = false
        }
    }

    override val layout: Int
        get() = R.layout.activity_login
    override val screenName: Int
        get() = R.string.analytics_title_login

    override val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appVersion.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)
        clickListenersSetup()
        loginUserObservable()
        if (BuildConfig.DEBUG) {
            phone.setText(R.string.test_phone_number)
            password.setText(R.string.test_password)
        }
    }

    override fun onDestroy() {
        if (progressDialog.isResumed) progressDialog.dismissAllowingStateLoss()
        super.onDestroy()
    }

    private fun clickListenersSetup() {
        loginButton.setOnClickListener {
            loginButton.isEnabled = false
            viewModel.login(phone.text.toString(), password.text.toString())
        }
    }

    private fun loginUserObservable() {
        viewModel.loggedIn().observe(this, Observer {
            it.handle(
                onSuccess = { activity ->
                    progressDialog.dismiss()
                    activity?.let(::startActivityWithoutTrace)
                },
                onFailure = {
                    // TODO: Handle errors to show personalized messages for each one
                    progressDialog.dismiss()
                    Snackbar.make(
                        loginButton,
                        getString(R.string.error_generic),
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                    loginButton.isEnabled = true
                },
                onLoading = {
                    progressDialog.show(supportFragmentManager, ProgressDialogFragment.TAG)
                }
            )
        })
    }
}
