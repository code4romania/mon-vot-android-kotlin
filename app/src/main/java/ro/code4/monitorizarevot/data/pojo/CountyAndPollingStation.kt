package ro.code4.monitorizarevot.data.pojo

import androidx.room.Relation
import ro.code4.monitorizarevot.data.model.County

class CountyAndPollingStation {
    var idPollingStation: Int = 0
    lateinit var countyCode: String
    lateinit var observerArrivalTime: String

    @Relation(parentColumn = "countyCode", entityColumn = "code", entity = County::class)
    lateinit var county: List<County>

    fun countyOrNull(): County? =
        if (::county.isInitialized && county.isNotEmpty()) county[0] else null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CountyAndPollingStation
        if (idPollingStation != other.idPollingStation) return false
        if (countyCode != other.countyCode) return false
        if (observerArrivalTime != other.observerArrivalTime) return false
        if (county != other.county) return false
        return true
    }

    override fun hashCode(): Int {
        var result = idPollingStation
        result = 31 * result + countyCode.hashCode()
        result = 31 * result + observerArrivalTime.hashCode()
        result = 31 * result + county.hashCode()
        return result
    }
}
