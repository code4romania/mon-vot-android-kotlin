package ro.code4.monitorizarevot.widget

import ro.code4.monitorizarevot.data.model.Answer
import ro.code4.monitorizarevot.data.model.response.ResponseAnswer


interface AnswerLayout {
    val answer: ResponseAnswer
    fun setAnswer(answer: Answer)
    fun setDetail(detail: String)
}
