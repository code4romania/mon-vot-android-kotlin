package ro.code4.monitorizarevot.data.pojo

import androidx.room.Embedded
import androidx.room.Relation
import ro.code4.monitorizarevot.data.model.answers.AnsweredQuestion
import ro.code4.monitorizarevot.data.model.answers.SelectedAnswer

class AnsweredQuestionPOJO {
    @Embedded
    lateinit var answeredQuestion: AnsweredQuestion
    @Relation(parentColumn = "id", entityColumn = "questionId")
    lateinit var selectedAnswers: List<SelectedAnswer>
}