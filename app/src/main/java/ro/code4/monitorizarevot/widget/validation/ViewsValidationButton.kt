package ro.code4.monitorizarevot.widget.validation

import android.content.Context
import android.util.AttributeSet
import android.widget.Button

class ViewsValidationButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {
    private var onChangeValidators = listOf<OnChangeValidator>()

    fun setValidators(vararg onChangeValidators: OnChangeValidator) {
        this.onChangeValidators = onChangeValidators.toList()
        onChangeValidators.forEach {
            it.onChangeListener = {
                validateViews()
            }
        }
    }

    private fun validateViews() {
        isEnabled = onChangeValidators.all { it.isValid() }
    }
}