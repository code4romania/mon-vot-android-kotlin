package ro.code4.monitorizarevot.adapters

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import ro.code4.monitorizarevot.adapters.delegates.MultiChoiceDelegate
import ro.code4.monitorizarevot.adapters.delegates.SingleChoiceDelegate
import ro.code4.monitorizarevot.adapters.helper.MultiChoiceListItem
import ro.code4.monitorizarevot.adapters.helper.QuestionDetailsListItem
import ro.code4.monitorizarevot.adapters.helper.SingleChoiceListItem
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.data.pojo.QuestionWithAnswers


class QuestionDetailsDelegationAdapter constructor(
    addNoteClickListener: (Question) -> Unit
) : AsyncListDifferDelegationAdapter<QuestionDetailsListItem>(DIFF_CALLBACK) {
    fun getItem(pos: Int): QuestionWithAnswers = items[pos].questionWithAnswers

    init {
        delegatesManager
            .addDelegate(SingleChoiceDelegate(addNoteClickListener))
            .addDelegate(MultiChoiceDelegate(addNoteClickListener))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<QuestionDetailsListItem>() {
            override fun areItemsTheSame(
                oldItem: QuestionDetailsListItem,
                newItem: QuestionDetailsListItem
            ): Boolean =
                when {
                    oldItem is SingleChoiceListItem && newItem is SingleChoiceListItem -> oldItem.questionWithAnswers.question.id == newItem.questionWithAnswers.question.id
                    oldItem is MultiChoiceListItem && newItem is MultiChoiceListItem -> oldItem.questionWithAnswers.question.id == newItem.questionWithAnswers.question.id
                    else -> false
                }

            override fun areContentsTheSame(
                oldItem: QuestionDetailsListItem,
                newItem: QuestionDetailsListItem
            ): Boolean =
                when {
                    oldItem is SingleChoiceListItem && newItem is SingleChoiceListItem -> oldItem.questionWithAnswers == newItem.questionWithAnswers
                    oldItem is MultiChoiceListItem && newItem is MultiChoiceListItem -> oldItem.questionWithAnswers == newItem.questionWithAnswers
                    else -> false
                }

        }
    }
}