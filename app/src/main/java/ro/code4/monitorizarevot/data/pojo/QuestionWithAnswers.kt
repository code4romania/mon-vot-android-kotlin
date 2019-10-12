package ro.code4.monitorizarevot.data.pojo

import androidx.room.Embedded
import androidx.room.Relation
import ro.code4.monitorizarevot.data.model.Answer
import ro.code4.monitorizarevot.data.model.Question

class QuestionWithAnswers {
    @Embedded
    lateinit var question: Question
    @Relation(parentColumn = "id", entityColumn = "questionId")
    var answers: List<Answer>? = null

    override fun equals(other: Any?): Boolean {
        return other is QuestionWithAnswers && question == other.question && answers == other.answers
    }

    override fun hashCode(): Int {
        var result = question.hashCode()
        result = 31 * result + answers.hashCode()
        return result
    }
}