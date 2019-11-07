package ro.code4.monitorizarevot.data.pojo

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.model.Section


class FormWithSections {
    @Embedded
    lateinit var form: FormDetails

    @Relation(parentColumn = "id", entityColumn = "formId", entity = Section::class)
    lateinit var sections: List<SectionWithQuestions>

    @Ignore
    var noAnsweredQuestions: Int = 0


    override fun equals(other: Any?): Boolean =
        other is FormWithSections && form == other.form && sections.map { sections } == other.sections.map { sections } && noAnsweredQuestions == other.noAnsweredQuestions

    override fun hashCode(): Int {
        var result = form.hashCode()
        result = 31 * result + sections.hashCode()
        result = 31 * result + noAnsweredQuestions.hashCode()
        return result
    }
}