package ro.code4.monitorizarevot.widget

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Checkable
import android.widget.CompoundButton
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.widget_checkbox_details.view.*
import ro.code4.monitorizarevot.R

class CheckBoxWithDetails : LinearLayout, Checkable {
    private val toggleEditTextVisibility =
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
    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int, @Suppress("UNUSED_PARAMETER") defStyleRes: Int
    ) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.widget_checkbox_details, this, true)

        orientation = VERTICAL
        answerCheckBox.setOnCheckedChangeListener(toggleEditTextVisibility)
    }

    fun setText(text: String) {
        answerCheckBox.text = text
    }

    fun setCheckedChangedListener(checkedChangedListener: CompoundButton.OnCheckedChangeListener) {
        answerCheckBox.setOnCheckedChangeListener { p0, p1 ->
            toggleEditTextVisibility.onCheckedChanged(p0, p1)
            checkedChangedListener.onCheckedChanged(p0, p1)
        }
    }

    fun setTextChangedListener(textChangedListener: TextWatcher) {
        answerDetails.addTextChangedListener(textChangedListener)
    }

    fun setValue(value: String?) {
        value?.let {
            answerDetails.setText(it)
        }
    }
}
