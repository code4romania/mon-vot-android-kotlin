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
import kotlinx.android.synthetic.main.fragment_guide.*
import org.koin.android.ext.android.inject
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.ui.base.ViewModelFragment
import ro.code4.monitorizarevot.widget.ProgressDialogFragment

class GuideFragment : ViewModelFragment<GuideViewModel>() {

    override val layout: Int
        get() = R.layout.fragment_guide

    override val screenName: Int
        get() = R.string.analytics_title_guide

    private var progressDialog: ProgressDialogFragment? = null
    override val viewModel: GuideViewModel by inject()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogFragment().also {
            it.isCancelable = false
        }

        webView.apply {
            settings.setSupportZoom(true)
            settings.javaScriptEnabled = true
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    if (progressDialog?.isResumed == true) {
                        progressDialog?.showNow(childFragmentManager, ProgressDialogFragment.TAG)
                    }
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    if (view.title.isNullOrEmpty()) {
                        view.reload()
                        return
                    }
                    progressDialog?.dismiss()
                }

                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    return request.url?.let { !it.path.equals(viewModel.url().value) } ?: true
                }
            }
        }
        viewModel.url().observe(viewLifecycleOwner, Observer {
            webView.loadUrl(it)
        })
    }

    override fun onDestroyView() {
        if (progressDialog?.isResumed == true) {
            progressDialog?.dismissAllowingStateLoss()
            progressDialog = null
        }
        super.onDestroyView()
    }
}