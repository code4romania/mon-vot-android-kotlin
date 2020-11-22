package ro.code4.monitorizarevot.adapters.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_note.*
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.adapters.helper.NoteListItem
import ro.code4.monitorizarevot.data.model.Note
import ro.code4.monitorizarevot.helper.formatNoteDateTime

class NoteDelegate(
    private val noteSelectedListener: (Note) -> Unit
) : AbsListItemAdapterDelegate<NoteListItem, ListItem, NoteDelegate.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder =
        ViewHolder(
            noteSelectedListener,
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

    class ViewHolder(
        private val noteSelectedListener: (Note) -> Unit,
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        private lateinit var item: NoteListItem
        private val noteRowContainer =
            containerView.findViewById<MaterialCardView>(R.id.noteRowContainer)

        fun bind(noteListItem: NoteListItem) {
            item = noteListItem
            noteRowContainer.setOnClickListener { noteSelectedListener(noteListItem.note) }
            formAndQuestionIdentifier.text = item.codes?.let { codes ->
                containerView.context.getString(
                    R.string.note_details_codes, codes.formCode, codes.questionCode
                )
            } ?: ""
            with(item.note) {
/*                questionId?.let {
                    noteQuestionText.visibility = VISIBLE
                    // TODO add question text here
                    // noteQuestionText.text = "Add question $questionId text here."
                }*/
                noteText.text = description
                noteDate.text = date.formatNoteDateTime()
            }
        }
    }
}