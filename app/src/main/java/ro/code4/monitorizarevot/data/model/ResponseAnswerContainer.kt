package ro.code4.monitorizarevot.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ro.code4.monitorizarevot.data.model.answers.AnsweredQuestion

class ResponseAnswerContainer {
    // TODO serialized names to be translated when api is updated
    @SerializedName("raspuns")
    @Expose
    lateinit var responseMapperList: List<AnsweredQuestion>
}

