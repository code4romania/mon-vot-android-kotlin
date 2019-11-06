package ro.code4.monitorizarevot.ui.base

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.firebase.messaging.RemoteMessage
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.LocaleManager
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

    fun showPushNotification(notification: RemoteMessage.Notification) {
        if (!isFinishing && dialog == null) {
            dialog = AlertDialog.Builder(this, R.style.AlertDialog)
                .setTitle(notification.title)
                .setMessage(notification.body)
                .setPositiveButton(R.string.push_notification_ok)
                { p0, _ -> p0.dismiss() }
                .setCancelable(false)
                .setOnDismissListener { dialog = null }
                .show()
        }

    }

}