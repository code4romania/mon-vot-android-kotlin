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
    var observerArrivalTime: String? = null

    @Expose
    var observerLeaveTime: String? = null

    @Expose
    var numberOfVotersOnTheList: Int = 0

    @Expose
    var numberOfCommissionMembers: Int = 0

    @Expose
    var numberOfFemaleMembers: Int = 0

    @Expose
    var minPresentMembers: Int = 0

    @Expose
    var chairmanPresence: Boolean = false

    @Expose
    var singlePollingStationOrCommission: Boolean = false

    @Expose
    var adequatePollingStationSize: Boolean = false



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
        arrivalTime: String?,
        leaveTime: String?,
        numberOfVotersOnTheList: Int,
        numberOfCommissionMembers: Int,
        numberOfFemaleMembers: Int,
        minPresentMembers: Int,
        chairmanPresence: Boolean,
        singlePollingStationOrCommission: Boolean,
        adequatePollingStationSize: Boolean
    ) : this(countyCode, municipalityCode, pollingStationNumber) {
        this.observerArrivalTime = arrivalTime
        this.observerLeaveTime = leaveTime

        this.numberOfVotersOnTheList = numberOfVotersOnTheList
        this.numberOfCommissionMembers=numberOfCommissionMembers
        this.numberOfFemaleMembers=numberOfFemaleMembers
        this.minPresentMembers=minPresentMembers
        this.chairmanPresence=chairmanPresence
        this.singlePollingStationOrCommission=singlePollingStationOrCommission
        this.adequatePollingStationSize=adequatePollingStationSize
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PollingStation

        if (id != other.id) return false
        if (municipalityCode != other.municipalityCode) return false
        if (pollingStationNumber != other.pollingStationNumber) return false
        if (observerArrivalTime != other.observerArrivalTime) return false
        if (observerLeaveTime != other.observerLeaveTime) return false
        if (numberOfVotersOnTheList!=other.numberOfVotersOnTheList) return false
        if (numberOfCommissionMembers!=other.numberOfCommissionMembers) return false
        if (numberOfFemaleMembers!=other.numberOfFemaleMembers) return false
        if (minPresentMembers!=other.minPresentMembers) return false
        if (chairmanPresence!=other.chairmanPresence) return false
        if (singlePollingStationOrCommission!=other.singlePollingStationOrCommission) return false
        if (adequatePollingStationSize!=other.adequatePollingStationSize) return false

        if (synced != other.synced) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + municipalityCode.hashCode()
        result = 31 * result + pollingStationNumber
        result = 31 * result + (observerArrivalTime?.hashCode() ?: 0)
        result = 31 * result + (observerLeaveTime?.hashCode() ?: 0)
        result = 31 * result + numberOfVotersOnTheList
        result = 31 * result + numberOfCommissionMembers
        result = 31 * result + numberOfFemaleMembers
        result = 31 * result + minPresentMembers
        result = 31 * result + chairmanPresence.hashCode()
        result = 31 * result + singlePollingStationOrCommission.hashCode()
        result = 31 * result + adequatePollingStationSize.hashCode()
        result = 31 * result + synced.hashCode()
        return result
    }


}