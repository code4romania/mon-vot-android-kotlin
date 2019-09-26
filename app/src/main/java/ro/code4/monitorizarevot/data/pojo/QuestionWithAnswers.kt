package ro.code4.monitorizarevot.data.pojo

import androidx.room.Embedded
import androidx.room.Relation
import ro.code4.monitorizarevot.data.model.Answer
import ro.code4.monitorizarevot.data.model.Question

class QuestionWithAnswers {
    @Embedded
    lateinit var question: Question
    @Relation(parentColumn = "id", entityColumn = "questionId")
    lateinit var answers: List<Answer>
}