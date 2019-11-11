package ro.code4.monitorizarevot.ui.guide

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_guide.*
import org.koin.android.ext.android.inject
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.WebClient
import ro.code4.monitorizarevot.ui.base.BaseAnalyticsFragment
import ro.code4.monitorizarevot.widget.ProgressDialogFragment

class GuideFragment : BaseAnalyticsFragment<GuideViewModel>(), WebClient.WebLoaderListener {


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView.settings.setSupportZoom(true)
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebClient(this)
        viewModel.url().observe(this, Observer {
            webView.loadUrl(it)
        })
    }

    override fun onPageFinished() {
        progressDialog.dismiss()
    }

    override fun onLoading() {
        if (!progressDialog.isResumed) {
            progressDialog.show(childFragmentManager, ProgressDialogFragment.TAG)
        }
    }

    override fun onDestroyView() {
        if (progressDialog.isResumed) {
            progressDialog.dismissAllowingStateLoss()
        }
        super.onDestroyView()
    }
}