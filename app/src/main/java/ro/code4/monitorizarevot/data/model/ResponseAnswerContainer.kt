package ro.code4.monitorizarevot.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ro.code4.monitorizarevot.data.model.answers.AnsweredQuestion

class ResponseAnswerContainer {

    @Expose
    lateinit var answers: List<AnsweredQuestion>
}

