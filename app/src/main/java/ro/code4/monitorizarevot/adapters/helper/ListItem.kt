package ro.code4.monitorizarevot.adapters.helper

import androidx.annotation.StringRes
import ro.code4.monitorizarevot.data.model.Note
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.data.pojo.FormWithSections
import ro.code4.monitorizarevot.data.pojo.QuestionWithAnswers

sealed class ListItem
class QuestionListItem(val question: Question) : ListItem()
open class QuestionDetailsListItem(open val questionWithAnswers: QuestionWithAnswers) : ListItem()
class SingleChoiceListItem(override val questionWithAnswers: QuestionWithAnswers) :
    QuestionDetailsListItem(questionWithAnswers)

class MultiChoiceListItem(override val questionWithAnswers: QuestionWithAnswers) :
    QuestionDetailsListItem(questionWithAnswers)
class SectionListItem(@param:StringRes val titleResourceId: Int, vararg val formatArgs: Any) :
    ListItem()

class FormListItem(val formWithSections: FormWithSections) : ListItem()
class AddNoteListItem : ListItem()
class NoteListItem(val note: Note) : ListItem()
