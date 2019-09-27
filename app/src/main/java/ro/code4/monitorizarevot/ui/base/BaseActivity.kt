package ro.code4.monitorizarevot.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ro.code4.monitorizarevot.interfaces.Layout
import ro.code4.monitorizarevot.interfaces.ViewModelSetter

abstract class BaseActivity<out T : BaseViewModel> : AppCompatActivity(), Layout,
    ViewModelSetter<T> {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
    }
}