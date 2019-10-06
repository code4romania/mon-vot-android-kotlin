package ro.code4.monitorizarevot.data.pojo

import androidx.room.Embedded
import androidx.room.Relation
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.data.model.Section

class SectionWithQuestions {
    @Embedded
    lateinit var section: Section
    @Relation(parentColumn = "uniqueId", entityColumn = "sectionId", entity = Question::class)
    lateinit var questions: List<QuestionWithAnswers>
}