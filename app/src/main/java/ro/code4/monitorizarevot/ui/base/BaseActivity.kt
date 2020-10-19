package ro.code4.monitorizarevot.ui.base

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.LocaleManager
import ro.code4.monitorizarevot.helper.collapseKeyboardIfFocusOutsideEditText
import ro.code4.monitorizarevot.helper.lifecycle.ActivityCallbacks
import ro.code4.monitorizarevot.interfaces.Layout
import ro.code4.monitorizarevot.interfaces.ViewModelSetter

abstract class BaseActivity<out T : BaseViewModel> : AppCompatActivity(), Layout,
    ViewModelSetter<T> {
    private val mCallbacks = ActivityCallbacks()
    internal var dialog: AlertDialog? = null
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

    //    Collapse the keyboard when the user taps outside the EditText
    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {

        currentFocus?.let { oldFocus ->
            super.dispatchTouchEvent(motionEvent)
            val newFocus = currentFocus ?: oldFocus
            collapseKeyboardIfFocusOutsideEditText(motionEvent, oldFocus, newFocus)
        }
        return super.dispatchTouchEvent(motionEvent)
    }
}