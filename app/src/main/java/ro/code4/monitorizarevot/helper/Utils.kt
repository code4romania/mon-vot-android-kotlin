package ro.code4.monitorizarevot.helper

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.amulyakhare.textdrawable.TextDrawable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ro.code4.monitorizarevot.R
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

fun Calendar?.getDateText(): String? {
    if (this == null) {
        return null
    }
    val formatter = SimpleDateFormat(Constants.DATE_FORMAT, Locale.US)
    return formatter.format(time)
}

fun Calendar.getTimeText(): String {
    val formatter = SimpleDateFormat(Constants.TIME_FORMAT, Locale.US)
    return formatter.format(time)
}

fun AppCompatActivity.callSupportCenter() {
    val callIntent = Intent(Intent.ACTION_DIAL)
    callIntent.data = Uri.parse("tel:" + Constants.SERVICE_CENTER_PHONE_NUMBER)
    startActivity(callIntent)
}

fun ImageView.setFormCode(context: Context, code: String?) {
    this.setImageDrawable(context.buildInitialsTextDrawable(code))

}

fun Context.buildInitialsTextDrawable(code: String?): Drawable {
    val size = resources.getDimensionPixelSize(R.dimen.form_icon_size)
    return TextDrawable.builder()
        .beginConfig()
        .bold()
        .toUpperCase()
        .width(size)
        .height(size)
        .fontSize(resources.getDimensionPixelSize(R.dimen.form_icon_code))
        .useFont(Typeface.SANS_SERIF)
        .textColor(ContextCompat.getColor(this, R.color.colorPrimary))
        .endConfig()
        .buildRect(code, ContextCompat.getColor(this, android.R.color.transparent))
}