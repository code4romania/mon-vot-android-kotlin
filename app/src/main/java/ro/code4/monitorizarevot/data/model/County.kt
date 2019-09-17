package ro.code4.monitorizarevot.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Parcel(Parcel.Serialization.FIELD)
class County {

    //    @PrimaryKey
    @Expose
    @SerializedName(ID_FIELD)
    var id: Int = 0

    @Expose
    @SerializedName(COUNTY_CODE_FIELD)
    var code: String? = null

    @Expose
    @SerializedName(COUNTY_NAME_FIELD)
    var name: String? = null

    @Expose
    @SerializedName(BRANCHES_COUNT_FIELD)
    var branchesCount: Int = 0

    companion object {

        const val ID_FIELD = "id"
        const val COUNTY_CODE_FIELD = "code"
        const val COUNTY_NAME_FIELD = "name"
        const val BRANCHES_COUNT_FIELD = "limit"
    }
}
