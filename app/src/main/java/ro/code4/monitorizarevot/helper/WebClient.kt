package ro.code4.monitorizarevot.helper

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient

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

    interface WebLoaderListener {
        fun onPageFinished()
        fun onLoading()
    }
}