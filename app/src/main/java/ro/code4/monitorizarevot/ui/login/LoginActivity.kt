package ro.code4.monitorizarevot.ui.login

import android.content.DialogInterface
import android.os.Bundle
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.BuildConfig
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.data.model.UpdateApp
import ro.code4.monitorizarevot.helper.TextWatcherDelegate
import ro.code4.monitorizarevot.helper.isOnline
import ro.code4.monitorizarevot.helper.openAppInPlayStore
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
            loginButton.isEnabled = true
        }

        phone.addTextChangedListener(object : TextWatcher by TextWatcherDelegate {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                loginButton.isEnabled = !p0.isNullOrEmpty() && !password.text.isNullOrEmpty()
            }
        })

        password.addTextChangedListener(object : TextWatcher by TextWatcherDelegate {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                loginButton.isEnabled = !p0.isNullOrEmpty() && !phone.text.isNullOrEmpty()
            }
        })
        observeAppUpdates()
    }

    private fun observeAppUpdates() {
        viewModel.shouldUpdateAppVersion().observe(this) { shouldUpdateApp(update = it) }
    }

    private fun shouldUpdateApp(update: UpdateApp) {
        if (update.hasUpdate) {
            AlertDialog.Builder(this)
                .setTitle(R.string.app_update_dialog_title)
                .setMessage(R.string.app_update_dialog_message)
                .setCancelable(update.needForceUpdate.not())
                .setPositiveButton(android.R.string.ok) { dialog: DialogInterface, _ ->
                    if (update.needForceUpdate) {
                        openAppInPlayStore()
                    }
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    override fun onDestroy() {
        if (progressDialog.isResumed) progressDialog.dismissAllowingStateLoss()
        super.onDestroy()
    }

    private fun clickListenersSetup() {
        loginButton.setOnClickListener {
            if (!isOnline()) {
                Snackbar.make(loginButton, getString(R.string.login_no_internet), Snackbar.LENGTH_SHORT)
                    .show()

                return@setOnClickListener
            }

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
                onFailure = {error ->
                    // TODO: Handle errors to show personalized messages for each one
                    progressDialog.dismiss()

                    handleThrowable(error) {
                        showDefaultErrorSnackBar(loginButton)
                    }

                    loginButton.isEnabled = true
                },
                onLoading = {
                    progressDialog.showNow(supportFragmentManager, ProgressDialogFragment.TAG)
                }
            )
        })
    }
}
