package ro.code4.monitorizarevot.helper

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

open class PermissionManager(private val activity: Activity, private val fragment: Fragment?) {

    companion object {
        @JvmStatic
        val PERMISSION_REQUEST = 320
    }

    fun hasPermissions(vararg permissionString: String): Boolean = permissionString.all {
        ContextCompat.checkSelfPermission(
            activity,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isAnyDenied(vararg permissionString: String): Boolean = permissionString.any {
        ContextCompat.checkSelfPermission(
            activity,
            it
        ) == PackageManager.PERMISSION_DENIED
    }


    fun checkPermissions(vararg permissionString: String, listener: PermissionListener) {
        if (hasPermissions(*permissionString)) {
            listener.onPermissionsGranted()
            return
        }
        if (isAnyDenied(*permissionString) && checkShouldShowRequestPermissionsRationale(*permissionString)) {
            listener.onPermissionDenied(*permissionString,
                permissionsDenied = permissionString.filter {
                    ContextCompat.checkSelfPermission(
                        activity,
                        it
                    ) == PackageManager.PERMISSION_DENIED
                })
            return
        }
        if (fragment != null) {
            fragment.requestPermissions(permissionString, PERMISSION_REQUEST)
        } else {
            ActivityCompat.requestPermissions(activity, permissionString, PERMISSION_REQUEST)
        }
    }

    fun requestPermissions(vararg permissionString: String) {
        ActivityCompat.requestPermissions(activity, permissionString, PERMISSION_REQUEST)
    }


    fun checkShouldShowRequestPermissionsRationale(vararg permissionString: String): Boolean {
        return permissionString.any {
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                it
            )
        }
    }

    interface PermissionListener {
        fun onPermissionsGranted()
        fun onPermissionDenied(vararg allPermissions: String, permissionsDenied: List<String>)
    }
}