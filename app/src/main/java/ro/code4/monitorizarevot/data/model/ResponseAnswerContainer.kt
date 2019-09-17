package ro.code4.monitorizarevot.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Parcel(Parcel.Serialization.FIELD)
class ResponseAnswerContainer {
    // TODO serialized names to be translated when api is updated
    @SerializedName("raspuns")
    @Expose
    var responseMapperList: List<QuestionAnswer>? = null
}

