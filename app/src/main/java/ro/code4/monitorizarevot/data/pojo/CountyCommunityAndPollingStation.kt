package ro.code4.monitorizarevot.data.pojo

import androidx.room.Relation
import ro.code4.monitorizarevot.data.model.Community
import ro.code4.monitorizarevot.data.model.County

class CountyCommunityAndPollingStation {
    var pollingStationNumber: Int = 0
    lateinit var countyCode: String
    lateinit var communityCode: String
    lateinit var observerArrivalTime: String

    @Relation(parentColumn = "countyCode", entityColumn = "code", entity = County::class)
    lateinit var county: List<County>

    @Relation(parentColumn = "communityCode", entityColumn = "code", entity = Community::class)
    lateinit var community: List<Community>

    fun countyOrNull(): County? =
        if (::community.isInitialized && community.isNotEmpty()) county[0] else null

    fun communityOrNull(): Community? =
        if (::community.isInitialized && community.isNotEmpty()) community[0] else null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CountyCommunityAndPollingStation
        if (pollingStationNumber != other.pollingStationNumber) return false
        if (countyCode != other.countyCode) return false
        if (communityCode != other.communityCode) return false
        if (observerArrivalTime != other.observerArrivalTime) return false
        if (community != other.community) return false
        return true
    }

    override fun hashCode(): Int {
        var result = pollingStationNumber
        result = 31 * result + communityCode.hashCode()
        result = 31 * result + observerArrivalTime.hashCode()
        result = 31 * result + community.hashCode()
        return result
    }
}
