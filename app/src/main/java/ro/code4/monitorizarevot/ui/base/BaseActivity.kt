package ro.code4.monitorizarevot.ui.base

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import ro.code4.monitorizarevot.helper.LocaleManager
import ro.code4.monitorizarevot.interfaces.Layout
import ro.code4.monitorizarevot.interfaces.ViewModelSetter

abstract class BaseActivity<out T : BaseViewModel> : AppCompatActivity(), Layout,
    ViewModelSetter<T> {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)

        viewModel.messageToast().observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.wrapContext(newBase))
    }
}