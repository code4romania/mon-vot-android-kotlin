package ro.code4.monitorizarevot.helper

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.*
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ro.code4.monitorizarevot.BuildConfig
import ro.code4.monitorizarevot.interfaces.ExcludeFromCodeCoverage
import ro.code4.monitorizarevot.ui.branch.BranchActivity
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
                    ro.code4.monitorizarevot.R.color.textSecondary
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
    getLocationOnScreen(lvLocationOnScreen);
    val middleX = lvLocationOnScreen[0] + width / 2;
    if (childCount > 0) {
        view.getLocationOnScreen(vLocationOnScreen);
        if (vLocationOnScreen[0] <= middleX && vLocationOnScreen[0] + view.width >= middleX) {
            return true;
        }
    }
    return false;
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
