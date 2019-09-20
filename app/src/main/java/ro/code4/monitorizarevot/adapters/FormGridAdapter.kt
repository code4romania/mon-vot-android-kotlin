package ro.code4.monitorizarevot.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_form_section.view.*
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.helper.buildInitialsTextDrawable

class FormGridAdapter(private val context: Context, private val items: ArrayList<ListItem>) :
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
        val pair: Pair<String, Drawable?> = when (item.type) {
            TYPE_FORM -> {

                val form = item.value as FormDetails
                holder.itemView.setOnClickListener { listener.onFormClick(form) }
                Pair(form.description, context.buildInitialsTextDrawable(form.code))
            }
            else -> {
                holder.itemView.setOnClickListener { listener.onNoteClick() }
                Pair(
                    context.getString(R.string.form_notes),
                    context.getDrawable(R.drawable.ic_notes)
                )
            }

        }
        holder.itemView.formDescription.text = pair.first
        holder.itemView.formImage.setImageDrawable(pair.second)
    }

    class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView)

    interface OnClickListener {
        fun onFormClick(form: FormDetails)
        fun onNoteClick()
    }
}