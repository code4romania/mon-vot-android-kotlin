package ro.code4.monitorizarevot.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Parcel(Parcel.Serialization.FIELD)
class Question {

 @Expose
 @SerializedName("idIntrebare")
 val id: Int? = null

 @Expose
 @SerializedName("textIntrebare")
 val text: String? = null

 @Expose
 @SerializedName("codIntrebare")
 val code: String? = null

 @Expose
 @SerializedName("idTipIntrebare")
 val typeId: Int? = null

 @Expose
 @SerializedName("raspunsuriDisponibile")
 private val answerList: List<Answer>? = null

 fun getAnswerList(): List<Answer>? {
  return answerList
 }
}