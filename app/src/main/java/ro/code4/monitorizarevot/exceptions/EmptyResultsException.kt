package ro.code4.monitorizarevot.exceptions

import java.lang.Exception

class EmptyResultsException(message: String, thr: Throwable? = null) : Exception(message, thr)