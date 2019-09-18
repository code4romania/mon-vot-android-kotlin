package ro.code4.monitorizarevot.helper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody


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