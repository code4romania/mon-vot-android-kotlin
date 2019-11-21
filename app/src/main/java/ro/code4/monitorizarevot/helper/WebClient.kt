package ro.code4.monitorizarevot.helper

import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import ro.code4.monitorizarevot.BuildConfig

class WebClient(private val listener: WebLoaderListener) : WebViewClient() {
    override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)
        if (view.title.isNullOrEmpty()) {
            view.reload()
            return
        }
        listener.onPageFinished()
    }

    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
        listener.onLoading()
        super.onPageStarted(view, url, favicon)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return request?.url?.path?.contains(BuildConfig.GUIDE_URL) == false

    }

    interface WebLoaderListener {
        fun onPageFinished()
        fun onLoading()
    }
}