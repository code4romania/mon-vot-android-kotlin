package ro.code4.monitorizarevot.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel
import ro.code4.monitorizarevot.data.model.response.ResponseAnswer

@Parcel(Parcel.Serialization.FIELD)
class QuestionAnswer {
    @Expose
    @SerializedName("codFormular")
    var formCode: String? = null
    // TODO serialized names to be translated when api is updated
    @Expose
    @SerializedName("idIntrebare")
    var questionId: Int? = null

    @Expose
    @SerializedName("codJudet")
    var countyCode: String? = null

    @Expose
    @SerializedName("numarSectie")
    var sectionNumber: Int? = null

    @Expose
    @SerializedName("optiuni")
    var options: List<ResponseAnswer>? = null

    fun setData(branchQuestionAnswer: BranchQuestionAnswer, formCode: String?) {
        this.questionId = branchQuestionAnswer.questionId
        this.countyCode = branchQuestionAnswer.countryCode
        this.sectionNumber = branchQuestionAnswer.branchNumber
        this.options = branchQuestionAnswer.answers
        this.formCode = formCode
    }

}
