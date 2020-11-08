package ro.code4.monitorizarevot.ui.base

import ro.code4.monitorizarevot.exceptions.ErrorCodes
import ro.code4.monitorizarevot.exceptions.RetrofitException
import ro.code4.monitorizarevot.helper.logE
import ro.code4.monitorizarevot.helper.startActivityWithoutTrace
import ro.code4.monitorizarevot.ui.login.LoginActivity

abstract class BaseViewModelFragment<out T : BaseViewModel> : ViewModelFragment<T>() {

    override fun onError(thr: Throwable) {
        when (thr) {
            is RetrofitException -> {
                processRetrofitException(thr)
            }
        }
    }

    protected open fun processRetrofitException(thr: RetrofitException) {
        when (thr.kind) {
            RetrofitException.Kind.HTTP -> {
                when (thr.response?.code()) {
                    ErrorCodes.UNAUTHORIZED -> {
                        startLoginActivity()
                    }
                    ErrorCodes.BAD_REQUEST -> {
                        logE(TAG, "unknown error.")
                    }
                    else -> {
                        logE(TAG, "unexpected exception.")
                    }
                }
            }

            RetrofitException.Kind.NETWORK -> {
                logE(TAG, "network error.")
            }

            RetrofitException.Kind.UNEXPECTED -> {
                logE(TAG, "unexpected error.")
            }
        }
    }

    private fun startLoginActivity() =
        activity?.startActivityWithoutTrace(activity = LoginActivity::class.java)

    companion object {
        const val TAG = "BaseViewModelFragment"
    }
}