package ro.code4.monitorizarevot.widget

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatRadioButton
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.data.model.Answer
import ro.code4.monitorizarevot.data.model.answers.SelectedAnswer


class AnswerRadioButton : AppCompatRadioButton, AnswerLayout {

    override val answer: SelectedAnswer
        get() = tag as SelectedAnswer

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
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        layoutParams = RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.MATCH_PARENT,
            resources.getDimensionPixelSize(R.dimen.button_height)
        )
        setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.question_option_text)
        )
    }

    override fun setAnswer(answer: Answer) {
        tag = SelectedAnswer()
        text = answer.text
    }

    override fun setDetail(detail: String) {

    }
}
