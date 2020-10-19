package ro.code4.monitorizarevot.ui.base

import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.exceptions.EmptyResultsException
import ro.code4.monitorizarevot.exceptions.ErrorCodes
import ro.code4.monitorizarevot.exceptions.RetrofitException
import ro.code4.monitorizarevot.helper.createAndShowDialog
import ro.code4.monitorizarevot.helper.logE
import ro.code4.monitorizarevot.helper.startActivityWithoutTrace
import ro.code4.monitorizarevot.ui.login.LoginActivity

abstract class BaseViewModelFragment<out T : BaseViewModel> : ViewModelFragment<T>() {

    override fun onError(thr: Throwable) {
        when (thr) {
            is RetrofitException -> {
                processRetrofitException(thr)
            }
            is EmptyResultsException -> {
                val messageId: String = getString(R.string.no_counties_found)
                activity?.createAndShowDialog(messageId, {
                    logE(TAG, "action needed.")
                })
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
                    ErrorCodes.UNKNOWN -> {
                        logE(TAG, "unknown error.")
                    }
                    else -> {
                        logE(TAG, "unexpected exception.")
                    }
                }
            }

            RetrofitException.Kind.NETWORK -> {
                logE(TAG, "network error.")
                //todo do something about it.
            }

            RetrofitException.Kind.UNEXPECTED -> {
                logE(TAG, "unexpected error.")
                //todo do something about it.
            }
        }
    }

    private fun startLoginActivity() =
        activity?.startActivityWithoutTrace(activity = LoginActivity::class.java)

    companion object {
        const val TAG = "BaseViewModelFragment"
    }
}