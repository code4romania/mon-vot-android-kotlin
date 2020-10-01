package ro.code4.monitorizarevot.ui.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import retrofit2.HttpException
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.APIError400
import ro.code4.monitorizarevot.helper.LocaleManager
import ro.code4.monitorizarevot.helper.collapseKeyboardOnOutsideTap
import ro.code4.monitorizarevot.helper.fromJson
import ro.code4.monitorizarevot.helper.lifecycle.ActivityCallbacks
import ro.code4.monitorizarevot.interfaces.Layout
import ro.code4.monitorizarevot.interfaces.ViewModelSetter


abstract class BaseActivity<out T : BaseViewModel> : AppCompatActivity(), Layout,
    ViewModelSetter<T> {
    private val mCallbacks = ActivityCallbacks()
    private var dialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleManager.wrapContext(this)
        setContentView(layout)
        application.registerActivityLifecycleCallbacks(mCallbacks)
        viewModel.messageToast().observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.wrapContext(newBase))
    }


    override fun onDestroy() {
        application.unregisterActivityLifecycleCallbacks(mCallbacks)
        if (dialog != null && dialog?.isShowing == true) {
            dialog?.dismiss()
            dialog = null
        }
        super.onDestroy()

    }

    fun showPushNotification(title: String, body: String, okListener: (() -> Unit)? = null, dismissListener: (() -> Unit)? = null) {
        if (!isFinishing && dialog == null) {
            dialog = AlertDialog.Builder(this, R.style.AlertDialog)
                .setTitle(title)
                .setMessage(body)
                .setPositiveButton(R.string.push_notification_ok)
                { p0, _ ->
                    okListener?.invoke()
                    p0.dismiss()
                }
                .setCancelable(false)
                .setOnDismissListener {
                    dismissListener?.invoke()
                    dialog = null
                }
                .show()
        }
    }

    fun handleThrowable(exception: Throwable, otherError: () -> Unit) {
        when (exception) {
            is HttpException -> {
                if (exception.code() == 400) {
                    val apiError400 = exception.response()?.errorBody()?.string()?.fromJson(Gson(), APIError400::class.java)

                    apiError400?.let {
                        if (it.error == null) {
                            otherError()

                            return
                        }

                        val s = SpannableString(it.error)

                        //added a TextView
                        val tx1 = TextView(this)
                        tx1.text = s
                        tx1.autoLinkMask = Activity.RESULT_OK
                        tx1.movementMethod = LinkMovementMethod.getInstance()
                        val valueInPixels = resources.getDimension(R.dimen.big_margin).toInt()
                        tx1.setPadding(valueInPixels, valueInPixels, valueInPixels, valueInPixels)

                        Linkify.addLinks(s, Linkify.PHONE_NUMBERS)
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle(getString(R.string.error_generic))
                            .setCancelable(false)
                            .setPositiveButton(R.string.push_notification_ok)
                            { p0, _ -> p0.dismiss() }
                            .setCancelable(false)
                            .setOnDismissListener { dialog = null }
                            .setView(tx1)
                            .show()

                    }
                } else {
                    otherError()
                }
            }
            else -> {
                otherError()
            }
        }
    }

    fun showDefaultErrorSnackBar(view: View) {
        showErrorSnackBar(view, getString(R.string.error_generic))
    }

    private fun showErrorSnackBar(view: View, text: String) {
        Snackbar.make(
            view,
            text,
            Snackbar.LENGTH_SHORT
        )
            .show()
    }

    //    Collapse the keyboard when the user taps outside the input area
    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        collapseKeyboardOnOutsideTap(this, motionEvent, currentFocus)
        return super.dispatchTouchEvent(motionEvent)
    }
}