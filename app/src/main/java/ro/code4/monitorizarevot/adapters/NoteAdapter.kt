package ro.code4.monitorizarevot.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_note.view.*
import kotlinx.android.synthetic.main.item_section.view.*
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.adapters.helper.ViewHolder
import ro.code4.monitorizarevot.data.model.Note
import ro.code4.monitorizarevot.helper.formatDate

class NoteAdapter(private val context: Context, private val items: ArrayList<ListItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        @JvmStatic
        val TYPE_NOTE = 0
        @JvmStatic
        val TYPE_SECTION = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = when (viewType) {
            TYPE_NOTE -> R.layout.item_note
            else -> R.layout.item_section
        }
        val view = LayoutInflater.from(context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size
    override fun getItemViewType(position: Int): Int = items[position].type
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (item.type) {
            TYPE_NOTE -> {
                val note = item.value as Note
                with(note) {
                    holder.itemView.noteText.text = description
                    holder.itemView.noteDate.text = date.formatDate()
                }
            }
            else -> {
                holder.itemView.setOnClickListener(null)
                val resId = item.value as Int
                holder.itemView.sectionName.text = context.getString(resId)
            }

        }
    }

}