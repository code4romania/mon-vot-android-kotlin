package ro.code4.monitorizarevot.ui.guide

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_guide.*
import org.koin.android.ext.android.inject
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.createAndShowDialog
import ro.code4.monitorizarevot.ui.base.BaseViewModelFragment
import ro.code4.monitorizarevot.exceptions.WebViewException
import ro.code4.monitorizarevot.helper.logE
import ro.code4.monitorizarevot.widget.ProgressDialogFragment

class GuideFragment : BaseViewModelFragment<GuideViewModel>() {

    override val layout: Int
        get() = R.layout.fragment_guide
    override val screenName: Int
        get() = R.string.analytics_title_guide
    private val progressDialog: ProgressDialogFragment by lazy {
        ProgressDialogFragment().also {
            it.isCancelable = false
        }
    }
    override val viewModel: GuideViewModel by inject()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView.apply {
            settings.setSupportZoom(true)
            settings.javaScriptEnabled = true
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    if (!progressDialog.isResumed) {
                        progressDialog.showNow(childFragmentManager, ProgressDialogFragment.TAG)
                    }
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    if (view.title.isNullOrEmpty()) {
                        view.reload()
                        return
                    }
                    progressDialog.dismiss()
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    return request.url?.let { !it.path.equals(viewModel.url().value) } ?: true
                }

                override fun onReceivedError(
                    view: WebView?,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String?
                ) {
                    var message: String? = description
                    if (TextUtils.isEmpty(message)) {
                        message = "Unknown Error"
                    }
                    onError(
                        WebViewException(
                            message!!,
                            errorCode
                        )
                    )
                }
            }
        }
        viewModel.url().observe(viewLifecycleOwner, Observer {
            webView.loadUrl(it)
        })
    }

    override fun onError(thr: Throwable) {
        logE(TAG, "Error loading the page:" + thr.message)
        var messageId: String = getString(R.string.error_generic_message)
        if (thr is WebViewException) {
            if (thr.message.contains("ERR_INTERNET_DISCONNECTED")) {
                messageId = getString(R.string.error_no_connection)
            }
        }

        progressDialog.dismiss()
        logE(TAG, messageId)
        //todo add action
    }

    override fun onDestroyView() {
        if (progressDialog.isResumed) {
            progressDialog.dismissAllowingStateLoss()
        }
        super.onDestroyView()
    }

    companion object {
        const val TAG = "GuideFragment"
    }
}