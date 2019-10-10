package ro.code4.monitorizarevot.widget.validation

import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Spinner
import java.util.ArrayList

class SpinnersValidator(
    private vararg val spinners: Spinner
) : OnChangeValidator {
    private val itemSelectedMultiListener = ItemSelectedMultiListener()
    override var onChangeListener: (() -> Unit)? = null

    init {
        spinners.forEach {
            itemSelectedMultiListener.addListener(it.onItemSelectedListener)
            itemSelectedMultiListener.addListener(object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    onChangeListener?.invoke()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            })
            it.onItemSelectedListener = itemSelectedMultiListener
        }
    }

    override fun isValid(): Boolean {
        return spinners.all { spinner ->
            spinner.selectedItem != null
        }
    }

    private class ItemSelectedMultiListener : OnItemSelectedListener {

        private val listeners = ArrayList<OnItemSelectedListener?>()

        fun addListener(listener: OnItemSelectedListener?) {
            listeners.add(listener)
        }

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            for (listener in listeners) {
                listener?.onItemSelected(parent, view, position, id)
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            for (listener in listeners) {
                listener?.onNothingSelected(parent)
            }
        }
    }
}