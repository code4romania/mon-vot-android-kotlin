package ro.code4.monitorizarevot.widget

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Checkable
import android.widget.CompoundButton
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.widget_checkbox_details.view.*
import ro.code4.monitorizarevot.R

class CheckBoxWithDetails : LinearLayout, Checkable {
    private val onCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { _, _ -> updateVisibility() }

    private fun updateVisibility() {
        val visibility = if (answerCheckBox.isChecked) {
            View.VISIBLE
        } else {
            View.GONE
        }
        answerDetails.visibility = visibility
    }

    override fun isChecked(): Boolean = answerCheckBox.isChecked

    override fun toggle() = answerCheckBox.toggle()


    override fun setChecked(p0: Boolean) {
        answerCheckBox.isChecked = p0
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
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.widget_radio_button_details, this, true)

        orientation = VERTICAL
        answerCheckBox.setOnCheckedChangeListener(onCheckedChangeListener)
    }

    fun setText(text: String) {
        answerCheckBox.text = text
    }

}
