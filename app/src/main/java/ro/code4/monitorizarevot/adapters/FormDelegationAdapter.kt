package ro.code4.monitorizarevot.adapters

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import ro.code4.monitorizarevot.adapters.delegates.AddNoteDelegate
import ro.code4.monitorizarevot.adapters.delegates.FormDelegate
import ro.code4.monitorizarevot.adapters.helper.AddNoteListItem
import ro.code4.monitorizarevot.adapters.helper.FormListItem
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.model.FormDetails

class FormDelegationAdapter(
    formClickListener: (FormDetails) -> Unit,
    noteClickListener: () -> Unit
) : AsyncListDifferDelegationAdapter<ListItem>(DIFF_CALLBACK) {
    init {
        delegatesManager
            .addDelegate(FormDelegate(formClickListener))
            .addDelegate(AddNoteDelegate(noteClickListener))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListItem>() {
            override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean =
                when {
                    oldItem is FormListItem && newItem is FormListItem -> oldItem.formWithSections.form.code == newItem.formWithSections.form.code
                    oldItem is AddNoteListItem && newItem is AddNoteListItem -> true
                    else -> false
                }

            override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean =
                when {
                    oldItem is FormListItem && newItem is FormListItem -> oldItem.formWithSections.form == newItem.formWithSections.form
                    oldItem is AddNoteListItem && newItem is AddNoteListItem -> true
                    else -> false
                }
        }
    }
}