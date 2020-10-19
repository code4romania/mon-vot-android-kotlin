package ro.code4.monitorizarevot.exceptions

import java.lang.Exception

/**
 * Exception thrown when there is an issue with webview, loading paage, etc.
 */
class WebViewException(override val message: String, val code: Int = -1) : Exception(message)