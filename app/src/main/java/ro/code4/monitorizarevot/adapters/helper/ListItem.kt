package ro.code4.monitorizarevot.adapters.helper

import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.data.model.Section
import ro.code4.monitorizarevot.data.pojo.FormWithSections
import ro.code4.monitorizarevot.data.pojo.QuestionWithAnswers

sealed class ListItem
class QuestionListItem(val question: Question) : ListItem()
class SectionListItem(val index: Int, val section: Section): ListItem()
class FormListItem(val formWithSections: FormWithSections): ListItem()
class NoteListItem : ListItem()
