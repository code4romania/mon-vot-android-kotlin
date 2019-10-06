package ro.code4.monitorizarevot.widget

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import android.widget.CompoundButton
import android.widget.LinearLayout
import ro.code4.monitorizarevot.helper.LayoutParser


class AnswerRadioGroup : LinearLayout, CompoundButton.OnCheckedChangeListener {

    val checkedId: Int?
        get() {
            val view = LayoutParser.build(object : LayoutParser.Processor {
                override fun process(view: View): Boolean {
                    if (view is Checkable) {
                        return view.isChecked
                    }
                    return false
                }
            }).parse(this)
            return view?.tag as Int
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init()
    }

    private fun init() {
        orientation = VERTICAL
    }

    override fun onCheckedChanged(checkedButton: CompoundButton, isChecked: Boolean) {
        if (isChecked) {
            LayoutParser.build(object : LayoutParser.Processor {
                override fun process(view: View): Boolean {
                    if (view is Checkable && view.tag != checkedButton.tag) {
                        view.isChecked = false
                    }
                    return false
                }
            }).parse(this)
        }
    }
}
