package ro.code4.monitorizarevot.widget

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView

/**
 * Custom layout that contains a fixed layout at the bottom of the screen and a ScrollView
 * that fills the remaining space on top
 *
 * Must contain exactly two children
 * - the first one is the content inside the ScrollView
 * - the second one is the bottom layout
 */
class ScrollViewWithFixedBottom : LinearLayout {
    private var scrollView: ScrollView? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(context)
    }

    private fun init(context: Context) {
        orientation = VERTICAL

        scrollView = ScrollView(context)
        scrollView!!.isFillViewport = true
        val scrollLayoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)

        scrollView!!.layoutParams = scrollLayoutParams
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (childCount != 2) throw RuntimeException("Layout must contain exactly two children")

        val scrollChildView = getChildAt(0)
        val bottomLayout = getChildAt(1)

        removeViewAt(1)
        removeViewAt(0)

        scrollView!!.addView(scrollChildView)

        addView(scrollView)
        addView(bottomLayout)
    }
}
