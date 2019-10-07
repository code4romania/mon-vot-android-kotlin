package ro.code4.monitorizarevot.ui.splashscreen

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.getToken
import ro.code4.monitorizarevot.ui.base.BaseActivity
import ro.code4.monitorizarevot.ui.login.LoginActivity
import ro.code4.monitorizarevot.ui.main.MainActivity

class SplashScreenActivity: BaseActivity<SplashScreenViewModel>() {
    private val sharedPreferences: SharedPreferences by inject()

    override val layout: Int
        get() = R.layout.activity_splash_screen
    override val viewModel: SplashScreenViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent: Intent

        // Check if user is logged in or not
        if (sharedPreferences.getToken() != null) {
            intent = Intent(this, MainActivity::class.java)
        } else {
            intent = Intent(this, LoginActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}