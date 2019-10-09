package ro.code4.monitorizarevot.widget.validation

interface OnChangeValidator {
    fun isValid(): Boolean

    var onChangeListener: (() -> Unit)?
}