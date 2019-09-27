package ro.code4.monitorizarevot.data.pojo

import androidx.room.Embedded
import androidx.room.Relation
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.model.Section


class FormWithSections {
    fun incrementNoOfAnsweredQuestions() {
        if (noAnsweredQuestions == null) {
            noAnsweredQuestions = 0
        }
        noAnsweredQuestions = noAnsweredQuestions!! + 1
    }

    @Embedded
    lateinit var form: FormDetails

    @Relation(parentColumn = "code", entityColumn = "formCode", entity = Section::class)
    lateinit var sections: List<SectionWithQuestions>

    private var noAnsweredQuestions: Int? = null
    @Suppress("unused")
    fun setNoAnsweredQuestions(value: Int?) {
        noAnsweredQuestions = value
    }

    fun getNoAnsweredQuestions(): Int = noAnsweredQuestions ?: 0

}