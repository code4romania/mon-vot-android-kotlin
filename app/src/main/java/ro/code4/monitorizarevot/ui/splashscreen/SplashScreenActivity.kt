package ro.code4.monitorizarevot.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.ui.base.BaseActivity
import ro.code4.monitorizarevot.ui.login.LoginActivity
import ro.code4.monitorizarevot.ui.main.MainActivity

class SplashScreenActivity: BaseActivity<SplashScreenViewModel>() {

    override val layout: Int
        get() = R.layout.activity_splash_screen
    override val viewModel: SplashScreenViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loginLiveData().observe(this, Observer { isLoggedIn ->
            val intent: Intent
            if (isLoggedIn == true) {
                intent = Intent(this, MainActivity::class.java)
            } else {
                intent = Intent(this, LoginActivity::class.java)
            }

            startActivity(intent)
            finish()
        })

        viewModel.checkLogin()
    }
}