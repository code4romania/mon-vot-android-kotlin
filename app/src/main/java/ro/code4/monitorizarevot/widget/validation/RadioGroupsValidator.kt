package ro.code4.monitorizarevot.widget.validation

import android.widget.RadioGroup

class RadioGroupsValidator(
    private vararg val radioGroups: RadioGroup
) : OnChangeValidator {
    override var onChangeListener: (() -> Unit)? = null

    init {
        radioGroups.forEach {
            it.setOnCheckedChangeListener { _, _ ->
                onChangeListener?.invoke()
            }
        }
    }

    override fun isValid(): Boolean {
        return radioGroups.all { radioGroup ->
            radioGroup.checkedRadioButtonId != -1
        }
    }
}