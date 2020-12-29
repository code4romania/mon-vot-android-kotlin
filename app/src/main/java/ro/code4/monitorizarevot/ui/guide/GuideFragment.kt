package ro.code4.monitorizarevot.ui.guide

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_guide.*
import org.koin.android.ext.android.inject
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.ui.base.ViewModelFragment


class GuideFragment : ViewModelFragment<GuideViewModel>() {

    override val layout: Int = R.layout.fragment_guide
    override val screenName: Int = R.string.analytics_title_guide

    override val viewModel: GuideViewModel by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView() {
        webView.apply {
            settings.setSupportZoom(true)
            settings.javaScriptEnabled = true
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    openProgressDialog()
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    if (view.title.isNullOrEmpty()) {
                        view.reload()
                        return
                    }
                    closeProgressDialog()
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    return request.url.path.equals(viewModel.url().value).not()
                }
            }
        }

        viewModel.url().observe(viewLifecycleOwner, Observer {
            webView.loadUrl(it)
        })
    }

    private fun openProgressDialog() = findNavController().navigate(R.id.openProgressDialog)
    private fun closeProgressDialog() = findNavController().popBackStack()

}