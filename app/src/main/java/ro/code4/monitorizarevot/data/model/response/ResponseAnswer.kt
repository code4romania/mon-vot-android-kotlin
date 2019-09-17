package ro.code4.monitorizarevot.data.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

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
//        this.countyCode = Preferences.getCountyCode() //TODO
//        this.branchNumber = Preferences.getBranchNumber()
    }

    constructor(optionId: Int?) : this() {
        this.optionId = optionId
    }

    constructor(optionId: Int?, response: String) : this(optionId) {
        this.value = response
    }
}
