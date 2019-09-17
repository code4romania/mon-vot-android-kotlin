package ro.code4.monitorizarevot.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Parcel(Parcel.Serialization.FIELD)
class Section {

    // TODO serialized names to be translated when api is updated
//    @PrimaryKey
    @Expose
    @SerializedName("idSectiune")
    var id: String? = null

    @Expose
    @SerializedName("codSectiune")
    var code: String? = null

    @Expose
    @SerializedName("descriere")
    var description: String? = null

    @Expose
    @SerializedName("intrebari")
    var questions: List<Question>? = null
}