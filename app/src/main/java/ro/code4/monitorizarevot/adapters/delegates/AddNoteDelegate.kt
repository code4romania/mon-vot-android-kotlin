package ro.code4.monitorizarevot.adapters.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_form_section.*
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.helper.AddNoteListItem
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.helper.highlight

class AddNoteDelegate(
    private val clickListener: () -> Unit
) : AbsListItemAdapterDelegate<AddNoteListItem, ListItem, AddNoteDelegate.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_form_section, parent, false),
            clickListener
        )

    override fun isForViewType(item: ListItem, items: MutableList<ListItem>, position: Int): Boolean =
        item is AddNoteListItem

    override fun onBindViewHolder(
        item: AddNoteListItem,
        holder: ViewHolder,
        payloads: MutableList<Any>
    ) {
        holder.bind()
    }

    class ViewHolder(override val containerView: View, clickListener: () -> Unit) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        init {
            containerView.setOnClickListener { clickListener() }
        }

        fun bind() {
            progress.visibility = View.INVISIBLE
            questionsAnswered.visibility = View.INVISIBLE
            with(formDescription) {
                text = context.highlight(context.getString(R.string.form_notes))
            }
            formIcon.setImageResource(R.drawable.ic_form_notes)
        }
    }
}