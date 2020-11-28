package ro.code4.monitorizarevot.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
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
@Parcel(Parcel.Serialization.FIELD)
class PollingStation() {

    @PrimaryKey
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PollingStation

        if (id != other.id) return false
        if (countyCode != other.countyCode) return false
        if (idPollingStation != other.idPollingStation) return false
        if (urbanArea != other.urbanArea) return false
        if (isPollingStationPresidentFemale != other.isPollingStationPresidentFemale) return false
        if (observerArrivalTime != other.observerArrivalTime) return false
        if (observerLeaveTime != other.observerLeaveTime) return false
        if (synced != other.synced) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + countyCode.hashCode()
        result = 31 * result + idPollingStation
        result = 31 * result + urbanArea.hashCode()
        result = 31 * result + isPollingStationPresidentFemale.hashCode()
        result = 31 * result + (observerArrivalTime?.hashCode() ?: 0)
        result = 31 * result + (observerLeaveTime?.hashCode() ?: 0)
        result = 31 * result + synced.hashCode()
        return result
    }


}
