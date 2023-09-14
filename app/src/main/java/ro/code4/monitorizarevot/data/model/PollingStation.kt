package ro.code4.monitorizarevot.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import org.parceler.Parcel

@Entity(
    tableName = "polling_station",
    indices = [Index(value = ["countyCode", "municipalityCode", "pollingStationNumber"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = Municipality::class,
        parentColumns = ["code"],
        childColumns = ["municipalityCode"],
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
    lateinit var municipalityCode: String

    @Expose
    var pollingStationNumber: Int = 0

    @Expose
    var isPollingStationPresidentFemale: Boolean = false

    @Expose
    var observerArrivalTime: String? = null

    @Expose
    var observerLeaveTime: String? = null

    var synced: Boolean = false


    constructor(countyCode: String, municipalityCode: String, pollingStationNumber: Int) : this() {
        this.countyCode = countyCode
        this.municipalityCode = municipalityCode
        this.pollingStationNumber = pollingStationNumber
        this.id = "$countyCode$municipalityCode$pollingStationNumber"
    }

    constructor(
        countyCode: String,
        municipalityCode: String,
        pollingStationNumber: Int,
        isFemale: Boolean,
        arrivalTime: String?,
        departureTime: String?
    ) : this(countyCode, municipalityCode, pollingStationNumber) {
        this.isPollingStationPresidentFemale = isFemale
        this.observerArrivalTime = arrivalTime
        this.observerLeaveTime = departureTime
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PollingStation

        if (id != other.id) return false
        if (municipalityCode != other.municipalityCode) return false
        if (pollingStationNumber != other.pollingStationNumber) return false
        if (isPollingStationPresidentFemale != other.isPollingStationPresidentFemale) return false
        if (observerArrivalTime != other.observerArrivalTime) return false
        if (observerLeaveTime != other.observerLeaveTime) return false
        if (synced != other.synced) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + municipalityCode.hashCode()
        result = 31 * result + pollingStationNumber
        result = 31 * result + isPollingStationPresidentFemale.hashCode()
        result = 31 * result + (observerArrivalTime?.hashCode() ?: 0)
        result = 31 * result + (observerLeaveTime?.hashCode() ?: 0)
        result = 31 * result + synced.hashCode()
        return result
    }


}
