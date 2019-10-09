package ro.code4.monitorizarevot.adapters.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_question.*
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.adapters.helper.QuestionListItem
import ro.code4.monitorizarevot.data.model.Question

class QuestionDelegate(
    private val clickListener: (Question) -> Unit
) : AbsListItemAdapterDelegate<QuestionListItem, ListItem, QuestionDelegate.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_question,
                parent,
                false
            ), clickListener
        )

    override fun isForViewType(item: ListItem, items: MutableList<ListItem>, position: Int): Boolean =
        item is QuestionListItem

    override fun onBindViewHolder(
        item: QuestionListItem,
        holder: ViewHolder,
        payloads: MutableList<Any>
    ) {
        holder.bind(item)
    }

    class ViewHolder(override val containerView: View, clickListener: (Question) -> Unit) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        lateinit var question: Question

        init {
            containerView.setOnClickListener { clickListener(question) }
        }

        fun bind(item: QuestionListItem) {
            question = item.question

            with(question) {
                if (savedLocally || synced) {
                    syncIcon.visibility = View.VISIBLE
                    syncIcon.setImageResource(if (synced) R.drawable.ic_synced else R.drawable.ic_sync_progress)
                } else {
                    syncIcon.visibility = View.INVISIBLE
                }
                questionCode.text = code
                questionText.text = text
            }
        }
    }
}