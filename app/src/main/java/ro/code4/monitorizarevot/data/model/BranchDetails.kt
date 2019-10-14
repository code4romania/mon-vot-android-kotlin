package ro.code4.monitorizarevot.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Entity(
    tableName = "branch_details",
    indices = [Index(value = ["countyCode", "branchNumber"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = County::class,
        parentColumns = ["code"],
        childColumns = ["countyCode"],
        onDelete = ForeignKey.CASCADE

    )]
)
@Parcel(Parcel.Serialization.FIELD) //TODO Syncable
class BranchDetails() {

    // TODO serialized names to be translated when api is updated
    @PrimaryKey
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

    var synced: Boolean = false


    constructor(countyCode: String, branchNumber: Int) : this() {
        this.countyCode = countyCode
        this.branchNumber = branchNumber
        this.id = "$countyCode$branchNumber"
    }

    constructor(
        countyCode: String,
        branchNumber: Int,
        isUrban: Boolean,
        isFemale: Boolean,
        arrivalTime: String?,
        departureTime: String?
    ) : this(countyCode, branchNumber) {
        this.isUrban = isUrban
        this.isFemale = isFemale
        this.arrivalTime = arrivalTime
        this.departureTime = departureTime
    }

}
