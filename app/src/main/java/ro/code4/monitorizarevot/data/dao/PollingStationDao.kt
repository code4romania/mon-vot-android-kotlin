package ro.code4.monitorizarevot.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Maybe
import io.reactivex.Observable
import ro.code4.monitorizarevot.data.model.PollingStation
import ro.code4.monitorizarevot.data.pojo.CountyCommunityAndPollingStation
import ro.code4.monitorizarevot.data.pojo.PollingStationInfo

@Dao
interface PollingStationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(pollingStation: PollingStation)

    @Update
    fun updatePollingStationDetails(pollingStation: PollingStation)

    @Query("SELECT * FROM polling_station WHERE countyCode=:countyCode AND communityCode=:communityCode AND pollingStationNumber=:pollingStationNumber")
    fun get(countyCode:String, communityCode: String, pollingStationNumber: Int): Maybe<PollingStation>

    @Query("SELECT county.name AS countyName, community.name AS communityName, polling_station.pollingStationNumber as pollingStationNumber FROM polling_station" +
            " INNER JOIN county on county.code=polling_station.countyCode " +
            " INNER JOIN community on community.code=polling_station.communityCode " +
            "WHERE county.code=:countyCode AND communityCode=:communityCode AND pollingStationNumber=:pollingStationNumber")
    fun getPollingStationInfo(
        countyCode: String,
        communityCode: String,
        pollingStationNumber: Int
    ): Maybe<PollingStationInfo>

    @Query("SELECT * FROM polling_station WHERE synced=:synced")
    fun getNotSyncedPollingStations(synced: Boolean = false): Maybe<List<PollingStation>>

    @Query("SELECT COUNT(*) FROM polling_station WHERE synced =:synced")
    fun getCountOfNotSyncedPollingStations(synced: Boolean = false): LiveData<Int>

    @Query("DELETE FROM polling_station")
    fun deleteAll()
    
    @Query("SELECT * FROM polling_station WHERE observerArrivalTime NOT NULL")
    fun getVisitedPollingStations(): Observable<List<CountyCommunityAndPollingStation>>
}