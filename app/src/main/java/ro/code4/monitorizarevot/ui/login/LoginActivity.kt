package ro.code4.monitorizarevot.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.MainActivity
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.data.model.User
import ro.code4.monitorizarevot.interfaces.Layout
import ro.code4.monitorizarevot.interfaces.ViewModelSetter

class LoginActivity : AppCompatActivity(), Layout, ViewModelSetter<LoginViewModel> {

    override val layout: Int
        get() = R.layout.activity_login
    override val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(layout)

        ButterKnife.bind(this)

        login_button.setOnClickListener {
            val user = User(phone.text.toString(), password.text.toString(), "1234")
            viewModel.login(user)
        }

        viewModel.loggedIn().observe(this, Observer {
            Toast.makeText(this, "Yaaay", Toast.LENGTH_SHORT).show()
            startMainActivity()
        })
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
