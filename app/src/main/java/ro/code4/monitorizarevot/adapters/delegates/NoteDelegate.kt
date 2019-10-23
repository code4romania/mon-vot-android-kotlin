package ro.code4.monitorizarevot.adapters.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_note.*
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.adapters.helper.NoteListItem
import ro.code4.monitorizarevot.helper.formatDate

class NoteDelegate : AbsListItemAdapterDelegate<NoteListItem, ListItem, NoteDelegate.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        )

    override fun isForViewType(
        item: ListItem,
        items: MutableList<ListItem>,
        position: Int
    ): Boolean =
        item is NoteListItem

    override fun onBindViewHolder(
        item: NoteListItem,
        holder: ViewHolder,
        payloads: MutableList<Any>
    ) {
        holder.bind(item)
    }

    class ViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        private lateinit var item: NoteListItem

        fun bind(noteListItem: NoteListItem) {
            item = noteListItem

            with(item.note) {
                noteText.text = description
                noteDate.text = date.formatDate()
            }
        }
    }
}