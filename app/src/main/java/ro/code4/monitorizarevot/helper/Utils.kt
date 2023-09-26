package ro.code4.monitorizarevot.helper

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.*
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ro.code4.monitorizarevot.BuildConfig
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.data.model.County
import ro.code4.monitorizarevot.data.model.Municipality
import ro.code4.monitorizarevot.data.model.Province
import ro.code4.monitorizarevot.helper.Constants.REQUEST_CODE_RECORD_VIDEO
import ro.code4.monitorizarevot.helper.Constants.REQUEST_CODE_TAKE_PHOTO
import ro.code4.monitorizarevot.interfaces.ExcludeFromCodeCoverage
import ro.code4.monitorizarevot.ui.section.PollingStationActivity
import ro.code4.monitorizarevot.ui.section.VisitedPollingStationsActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


fun String.createMultipart(name: String): MultipartBody.Part {
    val requestBody = toRequestBody("multipart/form-data".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(name, null, requestBody)
}

fun AppCompatActivity.replaceFragment(
    @IdRes layoutRes: Int, fragment: Fragment,
    bundle: Bundle? = null,
    tag: String? = null
) {
    supportFragmentManager.replaceFragment(layoutRes, fragment, bundle, tag)

}

fun FragmentManager.replaceFragment(
    @IdRes layoutRes: Int, fragment: Fragment,
    bundle: Bundle? = null,
    tag: String? = null,
    isPrimaryNavigationFragment: Boolean = false
) {
    val ft = beginTransaction()
    if (isPrimaryNavigationFragment) {
        ft.setPrimaryNavigationFragment(fragment)
    }
    bundle?.let {
        fragment.arguments = it
    }
    tag?.let {
        ft.addToBackStack(it)
    }
    ft.replace(layoutRes, fragment, tag)

    ft.commit()
}

fun AppCompatActivity.changePollingStation(province: Province? = null, county: County? = null, municipality: Municipality? = null, pollingStationNumber: Int = -1) {
    val intent = Intent(this, PollingStationActivity::class.java)
    if (province != null && county != null && municipality != null && pollingStationNumber > 0) {
        intent.putExtra(PollingStationActivity.EXTRA_POLLING_STATION_NUMBER, pollingStationNumber)
        intent.putExtra(PollingStationActivity.EXTRA_PROVINCE_NAME, province.name)
        intent.putExtra(PollingStationActivity.EXTRA_COUNTY_NAME, county.name)
        intent.putExtra(PollingStationActivity.EXTRA_MUNICIPALITY_NAME, municipality.name)
    }
    startActivity(intent)
}

fun AppCompatActivity.showVisitedPollingStations() {
    startActivity(Intent(this, VisitedPollingStationsActivity::class.java))
}

fun Calendar.updateTime(year: Int, month: Int, dayOfMonth: Int, hourOfDay: Int, minute: Int) {
    set(Calendar.YEAR, year)
    set(Calendar.MONTH, month)
    set(Calendar.DAY_OF_MONTH, dayOfMonth)
    set(Calendar.HOUR_OF_DAY, hourOfDay)
    set(Calendar.MINUTE, minute)
}

fun Calendar?.getDateText(): String? {
    if (this == null) {
        return null
    }
    val formatter = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
    return formatter.format(time)
}
fun Calendar?.getDateISO8601Text(): String? {
    if (this == null) {
        return null
    }
    val formatter = SimpleDateFormat(Constants.DATE_ISO_8601_FORMAT, Locale.getDefault())
    return formatter.format(time)
}

fun Calendar.getTimeText(): String {
    val formatter = SimpleDateFormat(Constants.DATE_TIME_FORMAT, Locale.getDefault())
    return formatter.format(time)
}

fun Date.formatDate(): String {
    val formatter = SimpleDateFormat(Constants.DATE_FORMAT_SIMPLE, Locale.getDefault())
    return formatter.format(this)
}

fun Date.formatDateTime(): String {
    val formatter = SimpleDateFormat(Constants.DATE_TIME_FORMAT, Locale.getDefault())
    return formatter.format(this)
}

fun Date.formatNoteDateTime(): String {
    val formatter = SimpleDateFormat(Constants.DATA_NOTE_FORMAT, Locale.getDefault())
    return formatter.format(this)
}

fun String?.getDate(): Long? {
    if (this == null) {
        return null
    }
    val formatter = SimpleDateFormat(Constants.DATE_ISO_8601_FORMAT, Locale.getDefault())

    return formatter.parse(this)?.time

}

fun AppCompatActivity.callSupportCenter() {
    val callIntent = Intent(Intent.ACTION_DIAL)
    callIntent.data = Uri.parse("tel:" + BuildConfig.SERVICE_CENTER_PHONE_NUMBER)
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
            ForegroundColorSpan(
                ContextCompat.getColor(
                    this,
                    R.color.textSecondary
                )
            ),
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

fun <A, B> zipLiveData(a: LiveData<A>, b: LiveData<B>): LiveData<Pair<A, B>> {
    return MediatorLiveData<Pair<A, B>>().apply {
        var lastA: A? = null
        var lastB: B? = null

        fun update() {
            val localLastA = lastA
            val localLastB = lastB
            if (localLastA != null && localLastB != null)
                this.value = Pair(localLastA, localLastB)
        }

        addSource(a) {
            lastA = it
            update()
        }
        addSource(b) {
            lastB = it
            update()
        }
    }
}

fun RecyclerView.getCenterXChildPosition(): Int {
    val childCount = childCount
    if (childCount > 0) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (isChildInCenterX(child)) {
                return getChildAdapterPosition(child)
            }
        }
    }
    return childCount
}

fun RecyclerView.isChildInCenterX(view: View): Boolean {
    val childCount = childCount
    val lvLocationOnScreen = IntArray(2)
    val vLocationOnScreen = IntArray(2)
    getLocationOnScreen(lvLocationOnScreen)
    val middleX = lvLocationOnScreen[0] + width / 2
    if (childCount > 0) {
        view.getLocationOnScreen(vLocationOnScreen)
        if (vLocationOnScreen[0] <= middleX && vLocationOnScreen[0] + view.width >= middleX) {
            return true
        }
    }
    return false
}

fun RecyclerView.addOnScrollListenerForGalleryEffect() {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        @ExcludeFromCodeCoverage
        override fun onScrolled(recyclerView: RecyclerView, i: Int, i2: Int) {
            val childCount = childCount
            val width = getChildAt(0).width
            val padding = (width - width) / 2
            for (j in 0 until childCount) {
                val v = recyclerView.getChildAt(j)
                //Left from padding to -(v.getWidth()-padding), from big to small
                var rate = 0f

                if (v.left <= padding) {
                    rate = if (v.left >= padding - v.width) {
                        (padding - v.left) * 1f / v.width
                    } else {
                        1f
                    }
                    v.scaleY = 1 - rate * 0.1f
                    v.scaleX = 1 - rate * 0.1f
                    v.alpha = 1 - rate * 0.1f

                } else {
                    //From right to padding to recyclerView.getWidth()-padding, from large to small
                    if (v.left <= recyclerView.width - padding) {
                        rate = (recyclerView.width - padding - v.left) * 1f / v.width
                    }
                    v.scaleY = 0.9f + rate * 0.1f
                    v.scaleX = 0.9f + rate * 0.1f
                    v.alpha = 0.9f + rate * 0.1f
                }
            }
        }

    })
}

fun RecyclerView.addOnLayoutChangeListenerForGalleryEffect() {
    addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
        @ExcludeFromCodeCoverage
        if (childCount < 3) {
            if (getChildAt(1) != null) {
                if (getCenterXChildPosition() == 0) {
                    val v1 = getChildAt(1)
                    v1.scaleY = 0.9f
                    v1.scaleX = 0.9f
                } else {
                    val v1 = getChildAt(0)
                    v1.scaleY = 0.9f
                    v1.scaleX = 0.9f
                }
            }
        } else {
            if (getChildAt(0) != null) {
                val v0 = getChildAt(0)
                v0.scaleY = 0.9f
                v0.scaleX = 0.9f
            }
            if (getChildAt(2) != null) {
                val v2 = getChildAt(2)
                v2.scaleY = 0.9f
                v2.scaleX = 0.9f
            }
        }

    }
}


val TextWatcherDelegate = object : TextWatcher {
    override fun afterTextChanged(s: Editable?) = Unit

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
}

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}

fun Fragment.openGallery() {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.type = "image/*"
    val extraMime = arrayOf("image/*", "video/*")
    intent.putExtra(Intent.EXTRA_MIME_TYPES, extraMime)
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.resolveActivity(activity!!.packageManager)?.also {
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.select_photo)),
            Constants.REQUEST_CODE_GALLERY
        )
    }
}

fun Fragment.openCamera(action: String, fileName: String, folder: String, requestCode: Int): File? {
    val intent = Intent(action)
    if (intent.resolveActivity(activity!!.packageManager) != null) {
        val file = activity?.createMediaFile(fileName, folder)
        if (file != null) {
            val uri = FileProvider.getUriForFile(activity!!, activity!!.packageName, file)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            if (action == MediaStore.ACTION_VIDEO_CAPTURE) {
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
            }
            startActivityForResult(intent, requestCode)
            return file
        }
    }
    return null
}

fun Fragment.takePicture(): File? {
    return openCamera(
        MediaStore.ACTION_IMAGE_CAPTURE,
        "IMG_${Calendar.getInstance().timeInMillis}.jpg",
        Environment.DIRECTORY_PICTURES,
        REQUEST_CODE_TAKE_PHOTO
    )
}

fun Fragment.takeVideo(): File? {
    return openCamera(
        MediaStore.ACTION_VIDEO_CAPTURE,
        "VID_${Calendar.getInstance().timeInMillis}.mp4",
        Environment.DIRECTORY_MOVIES,
        REQUEST_CODE_RECORD_VIDEO
    )

}

fun <T> LiveData<T>.observeOnce(observer: androidx.lifecycle.Observer<T>) {
    observeForever(object : androidx.lifecycle.Observer<T> {
        override fun onChanged(t: T) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun Context.createMediaFile(name: String, folder: String): File {
    val storageDir = File(getExternalFilesDir(folder), getString(R.string.app_name))
    if (!storageDir.exists()) {
        storageDir.mkdirs()    // result ignored
    }

    return File(storageDir, name)
}

fun Activity.startActivityWithoutTrace(activity: Class<*>) {
    startActivity(Intent(this, activity))
    finishAffinity()
}

fun Activity.hideKeyboard() {
    val view = currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun String.toHtml(): Spanned? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(this)
    }
}

/*
    The locale string can be:
    - [language_code]
    - [language_code]_[country_code]
 */
fun String.getLocale(): Locale {
    val parts = split("_")
    return if (parts.size == 2) {
        Locale(parts[0], parts[1])
    } else {
        Locale(parts[0])
    }
}

fun <T> String.fromJson(gson: Gson, clazz: Class<T>): T {
    return gson.fromJson(this, clazz)
}

fun Context.isOnline(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val n = cm.activeNetwork
        if (n != null) {
            val nc = cm.getNetworkCapabilities(n)
            //It will check for both wifi and cellular network
            return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI)
        }
        return false
    } else {
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}

fun Context.browse(url: String, newTask: Boolean = false): Boolean {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        return true
    } catch (e: ActivityNotFoundException) {
        return false
    }
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun FirebaseRemoteConfig?.getStringOrDefault(key: String, defaultValue: String) =
    this?.getString(key).takeUnless {
        it == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_STRING
    } ?: defaultValue

/*
 *  Hide software keyboard if user taps outside the EditText
 *  use inside override fun dispatchTouchEvent()
 */
fun collapseKeyboardIfFocusOutsideEditText(
        motionEvent: MotionEvent,
        oldFocusedView: View,
        newFocusedView: View
) {
    if (motionEvent.action == MotionEvent.ACTION_UP) {
        if (newFocusedView == oldFocusedView) {

            val srcCoordinates = IntArray(2)
            oldFocusedView.getLocationOnScreen(srcCoordinates)

            val rect = Rect(srcCoordinates[0], srcCoordinates[1], srcCoordinates[0] +
                    oldFocusedView.width, srcCoordinates[1] + oldFocusedView.height)

            if (rect.contains(motionEvent.x.toInt(), motionEvent.y.toInt()))
                return
        } else if (newFocusedView is EditText) {
            //  If new focus is other EditText then will not collapse
            return
        }

        // Collapse the keyboard from activity
        ContextCompat.getSystemService(newFocusedView.context, InputMethodManager::class.java)
                ?.hideSoftInputFromWindow(newFocusedView.windowToken, 0)
    }
}
