package ro.code4.monitorizarevot.data.pojo

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Embedded
import androidx.room.Relation
import ro.code4.monitorizarevot.data.model.Answer
import ro.code4.monitorizarevot.data.model.Question

class QuestionWithAnswers {
    @Embedded
    lateinit var question: Question
    @Relation(parentColumn = "id", entityColumn = "questionId")
    var answers: List<Answer>? = null


    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<QuestionWithAnswers>() {
            override fun areItemsTheSame(
                oldItem: QuestionWithAnswers,
                newItem: QuestionWithAnswers
            ): Boolean = oldItem.question.id == newItem.question.id


            override fun areContentsTheSame(
                oldItem: QuestionWithAnswers,
                newItem: QuestionWithAnswers
            ): Boolean = oldItem == newItem

        }
    }

    override fun equals(other: Any?): Boolean {
        return other is QuestionWithAnswers && question == other.question && answers == other.answers
    }

    override fun hashCode(): Int {
        var result = question.hashCode()
        result = 31 * result + answers.hashCode()
        return result
    }
}