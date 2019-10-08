package ro.code4.monitorizarevot.adapters

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import ro.code4.monitorizarevot.adapters.delegates.QuestionDelegate
import ro.code4.monitorizarevot.adapters.delegates.SectionDelegate
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.adapters.helper.QuestionListItem
import ro.code4.monitorizarevot.adapters.helper.SectionListItem
import ro.code4.monitorizarevot.data.model.Question

class QuestionDelegationAdapter(
    questionClickListener: (Question) -> Unit
) : AsyncListDifferDelegationAdapter<ListItem>(DIFF_CALLBACK) {
    init {
        delegatesManager
            .addDelegate(QuestionDelegate(questionClickListener))
            .addDelegate(SectionDelegate())
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListItem>() {
            override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean =
                when {
                    oldItem is QuestionListItem && newItem is QuestionListItem -> oldItem.question.id == newItem.question.id
                    oldItem is SectionListItem && newItem is SectionListItem -> oldItem.section.id == newItem.section.id
                    else -> false
                }

            override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean =
                when {
                    oldItem is QuestionListItem && newItem is QuestionListItem -> oldItem.question == newItem.question
                    oldItem is SectionListItem && newItem is SectionListItem -> oldItem.index == newItem.index && oldItem.section == newItem.section
                    else -> false
                }
        }
    }
}
