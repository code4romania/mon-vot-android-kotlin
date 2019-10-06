package ro.code4.monitorizarevot.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.data.model.User
import ro.code4.monitorizarevot.ui.base.BaseActivity
import ro.code4.monitorizarevot.ui.branch.BranchActivity
import ro.code4.monitorizarevot.ui.main.MainActivity

class LoginActivity : BaseActivity<LoginViewModel>() {

    override val layout: Int
        get() = R.layout.activity_login
    override val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginButton.setOnClickListener {
            val user = User(
                phone.text.toString(),
                password.text.toString(),
                "1234"
            )//TODO replace with phone uiid  Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            viewModel.login(user)
        }

        switchLanguageButton.setOnClickListener {
            switchLanguage()
        }

        viewModel.loggedIn().observe(this, Observer {
            startBranchActivity()
        })
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startBranchActivity() {
        startActivity(Intent(this, BranchActivity::class.java))
        finish()
    }
}
