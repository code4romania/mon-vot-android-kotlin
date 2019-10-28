package ro.code4.monitorizarevot.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ro.code4.monitorizarevot.R
import java.util.*


class LanguageAdapter(context: Context, private val languages: List<Locale>) :
    ArrayAdapter<Locale>(context, R.layout.support_simple_spinner_dropdown_item) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, parent)
    }

    private fun createItemView(position: Int, parent: ViewGroup): View {

        val view = LayoutInflater.from(context)
            .inflate(R.layout.support_simple_spinner_dropdown_item, parent, false)

        val language = languages[position]
        (view as TextView).text = language.displayLanguage
        return view
    }
}