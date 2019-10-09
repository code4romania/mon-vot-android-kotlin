package ro.code4.monitorizarevot.widget.validation

import android.text.TextWatcher
import android.widget.TextView
import ro.code4.monitorizarevot.helper.TextWatcherDelegate

class TextViewsValidator(
    private vararg val textViews: TextView
) : OnChangeValidator {
    override var onChangeListener: (() -> Unit)? = null

    init {
        textViews.forEach {
            it.addTextChangedListener(object : TextWatcher by TextWatcherDelegate {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    onChangeListener?.invoke()
                }
            })
        }
    }

    override fun isValid(): Boolean {
        return textViews.all { textView ->
            textView.text.isNotEmpty()
        }
    }
}