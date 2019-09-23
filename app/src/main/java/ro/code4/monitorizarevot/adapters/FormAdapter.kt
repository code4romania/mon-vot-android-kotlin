package ro.code4.monitorizarevot.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_form_section.view.*
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.helper.highlight

class FormAdapter(private val context: Context, private val items: ArrayList<ListItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var listener: OnClickListener

    companion object {
        const val TYPE_FORM = 0
        const val TYPE_NOTE = 1
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_form_section, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        val pair: Pair<CharSequence, Int> = when (item.type) {
            TYPE_FORM -> {

                val form = item.value as FormDetails
                holder.itemView.setOnClickListener { listener.onFormClick(form) }
                val prefix = form.description
                val suffix = context.getString(R.string.form_suffix, form.code)
                Pair(context.highlight(prefix, suffix), getIcon(form.code))
            }
            else -> {
                holder.itemView.setOnClickListener { listener.onNoteClick() }
                Pair(
                    context.highlight(context.getString(R.string.form_notes)),
                    R.drawable.ic_form_notes
                )
            }

        }
        holder.itemView.formDescription.text = pair.first
        holder.itemView.formIcon.setImageResource(pair.second)
    }

    private fun getIcon(code: String): Int {
        return when (code) {
            FormCode.A.name -> R.drawable.ic_form_a
            FormCode.B.name -> R.drawable.ic_form_b
            FormCode.C.name -> R.drawable.ic_form_c
            else -> R.drawable.ic_form_notes
        }
    }

    class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView)

    interface OnClickListener {
        fun onFormClick(form: FormDetails)
        fun onNoteClick()
    }

    enum class FormCode {
        A, B, C
    }
}