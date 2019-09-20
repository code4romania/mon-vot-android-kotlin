package ro.code4.monitorizarevot.ui.guide

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_guide.*
import org.koin.android.ext.android.inject
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.ui.base.BaseFragment

class GuideFragment : BaseFragment<GuideViewModel>() {
    override val layout: Int
        get() = R.layout.fragment_guide
    override val viewModel: GuideViewModel by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView.settings.setSupportZoom(true)
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebViewClient()
        viewModel.url().observe(this, Observer {
            webView.loadUrl(it)
        })
    }
}