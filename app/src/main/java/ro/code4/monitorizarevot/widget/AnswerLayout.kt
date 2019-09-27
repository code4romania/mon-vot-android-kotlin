package ro.code4.monitorizarevot.widget

import ro.code4.monitorizarevot.data.model.Answer
import ro.code4.monitorizarevot.data.model.answers.SelectedAnswer


interface AnswerLayout {
    val answer: SelectedAnswer
    fun setAnswer(answer: Answer)
    fun setDetail(detail: String)
}
