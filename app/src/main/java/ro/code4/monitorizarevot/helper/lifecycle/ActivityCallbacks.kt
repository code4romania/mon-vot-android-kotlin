package ro.code4.monitorizarevot.helper.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import ro.code4.monitorizarevot.App
import ro.code4.monitorizarevot.ui.base.BaseActivity

class ActivityCallbacks : Application.ActivityLifecycleCallbacks {

    override fun onActivityPaused(activity: Activity?) = Unit

    override fun onActivityResumed(activity: Activity?) = Unit

    override fun onActivityStarted(activity: Activity?) {
        if (activity is BaseActivity<*>) {
            App.instance.currentActivity = activity
        }

    }

    override fun onActivityDestroyed(activity: Activity?) = Unit

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) = Unit

    override fun onActivityStopped(activity: Activity?) = Unit

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) = Unit

}