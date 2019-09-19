package ro.code4.monitorizarevot.data.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

//@Entity(tableName = "response_answer")
@Parcel(Parcel.Serialization.FIELD)
class ResponseAnswer() {

    //TODO update fields to English names - to check names if influenced by backend

    //    @PrimaryKey
    @Expose
    @SerializedName("idOptiune")
    var optionId: Int? = null

    @Expose
    @SerializedName("value")
    var value: String? = null

    @SerializedName("codJudet")
    var countyCode: String? = null

    @SerializedName("numarSectie")
    var branchNumber: Int = 0

    init {

    }

    constructor(optionId: Int?) : this() {
        this.optionId = optionId
    }

}
