package ro.code4.monitorizarevot.data.pojo

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.model.Section


class FormWithSections {
    @Embedded
    lateinit var form: FormDetails

    @Relation(parentColumn = "code", entityColumn = "formCode", entity = Section::class)
    lateinit var sections: List<SectionWithQuestions>

    @Ignore
    var noAnsweredQuestions: Int = 0
}