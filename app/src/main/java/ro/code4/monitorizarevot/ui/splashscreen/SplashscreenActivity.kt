package ro.code4.monitorizarevot.ui.splashscreen

import android.os.Bundle
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.ui.base.BaseActivity

class SplashscreenActivity: BaseActivity<SplashscreenViewModel>()  {
    override val layout: Int
        get() = R.layout.activity_splashscreen
    override val viewModel: SplashscreenViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}