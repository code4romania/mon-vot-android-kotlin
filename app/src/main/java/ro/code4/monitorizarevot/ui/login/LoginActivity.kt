package ro.code4.monitorizarevot.ui.login

import android.content.res.Configuration
import android.os.Bundle
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.BuildConfig
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.*
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
            if(isPortrait())
            loginButton.isEnabled = true
        }
        if(isPortrait()) {
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

        setupHideKeyboardListeners(findViewById(R.id.login_root_layout))
    }

    override fun onDestroy() {
        if (progressDialog.isResumed) progressDialog.dismissAllowingStateLoss()
        super.onDestroy()
    }

    private fun clickListenersSetup() {

            val keyboardListener = TextView.OnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!isOnline()) {
                        Snackbar.make(
                            v,
                            getString(R.string.login_no_internet),
                            Snackbar.LENGTH_SHORT
                        )
                            .show()

                        return@OnEditorActionListener false
                    }
                    if(!phone.text.isNullOrBlank() && !password.text.isNullOrBlank())
                    viewModel.login(phone.text.toString(), password.text.toString())

                }
                false
            }
            password.setOnEditorActionListener(keyboardListener)
            phone.setOnEditorActionListener(keyboardListener)



                loginButton.setOnClickListener {
                    if (!isOnline()) {
                        Snackbar.make(
                            loginButton,
                            getString(R.string.login_no_internet),
                            Snackbar.LENGTH_SHORT
                        )
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

    /**
     * This code attaches a touchListener to every non-text view in the activity which closes the keyboard when not touched
     */
    private fun setupHideKeyboardListeners(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener(OnTouchListener { v, event ->
                hideKeyboard()
                false
            })
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupHideKeyboardListeners(innerView)
            }
        }
    }


}
