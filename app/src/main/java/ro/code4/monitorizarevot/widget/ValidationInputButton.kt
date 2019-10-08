package ro.code4.monitorizarevot.widget

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.Button
import android.widget.EditText
import ro.code4.monitorizarevot.helper.TextWatcherDelegate

class ValidationInputButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {

    fun observeInputFields(vararg inputFields: EditText) {
        inputFields.forEach {
            it.addTextChangedListener(object : TextWatcher by TextWatcherDelegate {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    isEnabled = inputFields.all { editText -> editText.text.isNotEmpty() }
                }
            })
        }
    }
}