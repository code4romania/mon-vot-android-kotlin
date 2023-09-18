package ro.code4.monitorizarevot.data.pojo

import androidx.room.Relation
import ro.code4.monitorizarevot.data.model.Municipality
import ro.code4.monitorizarevot.data.model.County

class CountyMunicipalityAndPollingStation {
    var pollingStationNumber: Int = 0
    lateinit var countyCode: String
    lateinit var municipalityCode: String
    lateinit var observerArrivalTime: String

    @Relation(parentColumn = "countyCode", entityColumn = "code", entity = County::class)
    lateinit var county: List<County>

    @Relation(parentColumn = "municipalityCode", entityColumn = "code", entity = Municipality::class)
    lateinit var municipality: List<Municipality>

    fun countyOrNull(): County? =
        if (::municipality.isInitialized && municipality.isNotEmpty()) county[0] else null

    fun countyAndMunicipalityAsTextOrNull(): Pair<String, String>? =
        if (::county.isInitialized && county.isNotEmpty() && ::municipality.isInitialized && municipality.isNotEmpty()) Pair(county[0].name, municipality[0].name) else null

    fun municipalityOrNull(): Municipality? =
        if (::municipality.isInitialized && municipality.isNotEmpty()) municipality[0] else null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CountyMunicipalityAndPollingStation
        if (pollingStationNumber != other.pollingStationNumber) return false
        if (countyCode != other.countyCode) return false
        if (municipalityCode != other.municipalityCode) return false
        if (observerArrivalTime != other.observerArrivalTime) return false
        if (municipality != other.municipality) return false
        return true
    }

    override fun hashCode(): Int {
        var result = pollingStationNumber
        result = 31 * result + municipalityCode.hashCode()
        result = 31 * result + observerArrivalTime.hashCode()
        result = 31 * result + municipality.hashCode()
        return result
    }
}