package ro.code4.monitorizarevot.adapters

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import ro.code4.monitorizarevot.adapters.delegates.NoteDelegate
import ro.code4.monitorizarevot.adapters.delegates.SectionDelegate
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.adapters.helper.NoteListItem
import ro.code4.monitorizarevot.adapters.helper.SectionListItem
import ro.code4.monitorizarevot.data.model.Note

class NoteDelegationAdapter(
    private val noteListener: (Note) -> Unit
) : AsyncListDifferDelegationAdapter<ListItem>(DIFF_CALLBACK) {
    init {
        delegatesManager
            .addDelegate(SectionDelegate())
            .addDelegate(NoteDelegate(noteListener))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListItem>() {
            override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean =
                when {
                    oldItem is NoteListItem && newItem is NoteListItem -> oldItem.note.id == newItem.note.id
                    oldItem is SectionListItem && newItem is SectionListItem -> oldItem.titleResourceId == newItem.titleResourceId
                    else -> false
                }

            override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean =
                when {
                    oldItem is NoteListItem && newItem is NoteListItem -> oldItem.note == newItem.note
                    oldItem is SectionListItem && newItem is SectionListItem -> oldItem.titleResourceId == newItem.titleResourceId && oldItem.formatArgs contentDeepEquals newItem.formatArgs
                    else -> false
                }
        }
    }
}