package ro.code4.monitorizarevot.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Entity(
    tableName = "polling_station",
    indices = [Index(value = ["countyCode", "idPollingStation"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = County::class,
        parentColumns = ["code"],
        childColumns = ["countyCode"],
        onDelete = ForeignKey.CASCADE

    )]
)
@Parcel(Parcel.Serialization.FIELD) //TODO Syncable
class PollingStation() {

    // TODO serialized names to be translated when api is updated
    @PrimaryKey
    @SerializedName("id")
    lateinit var id: String

    @Expose
    lateinit var countyCode: String

    @Expose
    var idPollingStation: Int = 0

    @Expose
    var urbanArea: Boolean = false

    @Expose
    var isPollingStationPresidentFemale: Boolean = false

    @Expose
    var observerArrivalTime: String? = null

    @Expose
    var observerLeaveTime: String? = null

    var synced: Boolean = false


    constructor(countyCode: String, pollingStationNumber: Int) : this() {
        this.countyCode = countyCode
        this.idPollingStation = pollingStationNumber
        this.id = "$countyCode$pollingStationNumber"
    }

    constructor(
        countyCode: String,
        pollingStationNumber: Int,
        isUrban: Boolean,
        isFemale: Boolean,
        arrivalTime: String?,
        departureTime: String?
    ) : this(countyCode, pollingStationNumber) {
        this.urbanArea = isUrban
        this.isPollingStationPresidentFemale = isFemale
        this.observerArrivalTime = arrivalTime
        this.observerLeaveTime = departureTime
    }

}
