package ro.code4.monitorizarevot.helper

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.ui.branch.BranchActivity
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

fun AppCompatActivity.changeBranch() {
    startActivity(Intent(this, BranchActivity::class.java))
    finishAffinity()
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

fun String?.getDate(): Long? {
    if (this == null) {
        return null
    }
    val formatter = SimpleDateFormat(Constants.DATE_FORMAT, Locale.US)

    return formatter.parse(this)?.time

}

fun AppCompatActivity.callSupportCenter() {
    val callIntent = Intent(Intent.ACTION_DIAL)
    callIntent.data = Uri.parse("tel:" + Constants.SERVICE_CENTER_PHONE_NUMBER)
    startActivity(callIntent)
}

fun Context.highlight(prefix: String, suffix: String? = null): CharSequence {
    val nonHighlighted = SpannableString(prefix)
    nonHighlighted.setSpan(
        StyleSpan(Typeface.BOLD),
        0,
        prefix.length,
        Spannable.SPAN_INCLUSIVE_INCLUSIVE
    )
    val builder = SpannableStringBuilder()
    builder.append(nonHighlighted)
    suffix?.let {
        val highlighted = SpannableString(suffix)
        highlighted.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.textSecondary)),
            0,
            suffix.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        highlighted.setSpan(
            StyleSpan(
                Typeface.createFromAsset(
                    assets,
                    "fonts/SourceSansPro-Light.ttf"
                ).style
            ),  //TODO make text light
            0,
            suffix.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        builder.append(highlighted)
    }


    return builder

}
