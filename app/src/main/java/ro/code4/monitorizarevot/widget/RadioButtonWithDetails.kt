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
import kotlinx.android.synthetic.main.widget_radio_button_details.view.*
import ro.code4.monitorizarevot.R


class RadioButtonWithDetails : LinearLayout, Checkable {
    private val toggleEditTextVisibility =
        CompoundButton.OnCheckedChangeListener { _, _ -> updateVisibility() }

    private fun updateVisibility() {
        val visibility = if (answerRadioButton.isChecked) {
            View.VISIBLE
        } else {
            View.GONE
        }
        answerDetails.visibility = visibility
    }

    override fun isChecked(): Boolean = answerRadioButton.isChecked

    override fun toggle() = answerRadioButton.toggle()


    override fun setChecked(p0: Boolean) {
        answerRadioButton.isChecked = p0
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
        answerRadioButton.setOnCheckedChangeListener(toggleEditTextVisibility)
    }

    fun setText(text: String) {
        answerRadioButton.text = text
    }

    override fun setTag(tag: Any?) {
        answerRadioButton.tag = tag
    }


    override fun getTag(): Any = answerRadioButton.tag
    fun setCheckedChangedListener(checkedChangedListener: CompoundButton.OnCheckedChangeListener) {
        answerRadioButton.setOnCheckedChangeListener { p0, p1 ->
            toggleEditTextVisibility.onCheckedChanged(p0, p1)
            checkedChangedListener.onCheckedChanged(p0, p1)
        }
    }

}
