package ro.code4.monitorizarevot.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import ro.code4.monitorizarevot.data.model.County


class CountyAdapter(context: Context, textViewResourceId: Int, items: List<County>) :
    ArrayAdapter<County>(context, textViewResourceId, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val result = super.getView(position, convertView, parent) as TextView
        result.text = getTextFor(position)
        return result
    }


    override fun getDropDownView(
        position: Int,
        convertView: View, @NonNull parent: ViewGroup
    ): View {
        val result = super.getDropDownView(position, convertView, parent) as TextView
        result.text = getTextFor(position)
        return result
    }

    private fun getTextFor(position: Int): String {
        val county = getItem(position)
        return county?.name ?: ""
    }

    override fun getPosition(@Nullable item: County?): Int {
        if (item == null) {
            return -1
        }

        for (i in 0 until count) {
            val registeredItem = getItem(i)
            if (registeredItem?.id == item.id) {
                return i
            }
        }

        return super.getPosition(item)
    }
}
