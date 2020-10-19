package ro.code4.monitorizarevot.ui.login

import android.os.Bundle
import android.text.TextWatcher
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.viewmodel.ext.android.viewModel
import retrofit2.HttpException
import retrofit2.http.HTTP
import ro.code4.monitorizarevot.BuildConfig
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.exceptions.ErrorCodes
import ro.code4.monitorizarevot.exceptions.RetrofitException
import ro.code4.monitorizarevot.helper.*
import ro.code4.monitorizarevot.ui.base.BaseAnalyticsActivity
import ro.code4.monitorizarevot.widget.ProgressDialogFragment
import java.io.IOException

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
                onFailure = { error ->
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

    private fun handleThrowable(exception: Throwable, fallback: (exception: Throwable) -> Unit) {
        when (exception) {
            is RetrofitException -> {
                when (exception.kind) {
                    RetrofitException.Kind.HTTP -> {
                        processHttpException(exception, fallback)
                    }
                    RetrofitException.Kind.NETWORK -> {
                        var messageId: String = getString(R.string.error_generic)
                        if (!isOnline()) {
                            messageId = getString(R.string.login_no_internet)
                        }

                        Snackbar.make(
                            loginButton,
                            messageId,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        fallback(exception)
                    }
                }
            }
        }
    }

    private fun processHttpException(
        exception: RetrofitException,
        fallback: (exception: Throwable) -> Unit
    ) {
        val message = exception.message ?: getString(R.string.error_generic)
        when (exception.response?.code()) {
            ErrorCodes.UNKNOWN -> {
                createAndShowDialog(
                    message, {
                        dialog = null
                    },
                    getString(R.string.login_no_internet)
                )
            }
            ErrorCodes.UNAUTHORIZED -> {
                createAndShowDialog(
                    message, {
                        dialog = null
                    },
                    getString(R.string.login_unauthorized)
                )
            }
            else -> {
                fallback(exception)
            }
        }
    }
}
