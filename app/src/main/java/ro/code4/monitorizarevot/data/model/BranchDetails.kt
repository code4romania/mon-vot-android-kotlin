package ro.code4.monitorizarevot.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

//@Entity(tableName = "branch_details")
@Parcel(Parcel.Serialization.FIELD) //TODO Syncable
class BranchDetails {

    // TODO serialized names to be translated when api is updated
//    @PrimaryKey
    @SerializedName("id")
    lateinit var id: String

    @Expose
    @SerializedName("codJudet")
    lateinit var countyCode: String

    @Expose
    @SerializedName("numarSectie")
    var branchNumber: Int = 0

    @Expose
    @SerializedName("esteZonaUrbana")
    var isUrban: Boolean = false

    @Expose
    @SerializedName("presedinteBesvesteFemeie")
    var isFemale: Boolean = false

    @Expose
    @SerializedName("oraSosirii")
    var arrivalTime: String? = null

    @Expose
    @SerializedName("oraPlecarii")
    var departureTime: String? = null

    var isSynced: Boolean = false

    constructor() {

    }

//    constructor(countyCode: String, branchNumber: Int) {
//        this.countyCode = countyCode
//        this.branchNumber = branchNumber
//        this.id = countyCode + branchNumber.toString()
//    }
//
//    constructor(
//        countyCode: String,
//        branchNumber: Int,
//        isUrban: Boolean,
//        isFemale: Boolean,
//        timeEnter: String,
//        timeLeave: String
//    ) : this(countyCode, branchNumber) {
//        this.isUrban = isUrban
//        this.isFemale = isFemale
//        this.timeEnter = timeEnter
//        this.timeLeave = timeLeave
//    }

}
