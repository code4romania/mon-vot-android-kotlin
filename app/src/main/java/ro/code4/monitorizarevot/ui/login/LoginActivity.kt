package ro.code4.monitorizarevot.ui.login

import android.os.Bundle
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.BuildConfig
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.data.model.User
import ro.code4.monitorizarevot.helper.startActivityWithoutTrace
import ro.code4.monitorizarevot.ui.base.BaseActivity
import ro.code4.monitorizarevot.widget.ProgressDialogFragment
import ro.code4.monitorizarevot.widget.validation.TextViewsValidator

class LoginActivity : BaseActivity<LoginViewModel>() {

    private val progressDialog: ProgressDialogFragment by lazy { ProgressDialogFragment() }

    override val layout: Int
        get() = R.layout.activity_login
    override val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appVersion.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)
        loginButton.setValidators(TextViewsValidator(phone, password))
        clickListenersSetup()
        loginUserObservable()
    }

    override fun onDestroy() {
        if (progressDialog.isResumed) progressDialog.dismissAllowingStateLoss()
        super.onDestroy()
    }

    private fun clickListenersSetup() {
        loginButton.setOnClickListener {
            val user = User(
                phone.text.toString(),
                password.text.toString(),
                "1234"
            )//TODO replace with phone uiid  Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            viewModel.login(user)
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
                    Snackbar.make(loginButton, "Something went wrong!", Snackbar.LENGTH_SHORT)
                        .show()
                },
                onLoading = {
                    progressDialog.show(supportFragmentManager, ProgressDialogFragment.TAG)
                }
            )
        })
    }
}
