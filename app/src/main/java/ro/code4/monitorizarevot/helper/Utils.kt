package ro.code4.monitorizarevot.helper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.*


fun String.createMultipart(name: String): MultipartBody.Part {
    val requestBody = toRequestBody("multipart/form-data".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(name, null, requestBody)
}

fun AppCompatActivity.replaceFragment(
    containerId: Int,
    newFragment: Fragment,
    bundle: Bundle? = null,
    tag: String? = null
) {
    val ft = supportFragmentManager.beginTransaction()
    bundle?.let {
        newFragment.arguments = it
    }

    ft.replace(containerId, newFragment)
    tag?.let {
        ft.addToBackStack(it)
    }
    ft.commit()
}

fun Calendar.updateTime(hourOfDay: Int, minute: Int) {
    set(Calendar.HOUR_OF_DAY, hourOfDay)
    set(Calendar.MINUTE, minute)
}

fun Calendar.getDateText(): String {
    val formatter = SimpleDateFormat(Constants.DATE_FORMAT, Locale.US)
    return formatter.format(time)
}

fun Calendar.getTimeText(): String {
    val formatter = SimpleDateFormat(Constants.TIME_FORMAT, Locale.US)
    return formatter.format(time)
}