package ro.code4.monitorizarevot.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Maybe
import io.reactivex.Observable
import ro.code4.monitorizarevot.data.model.PollingStation
import ro.code4.monitorizarevot.data.pojo.PollingStationInfo

@Dao
interface PollingStationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(pollingStation: PollingStation)

    @Update
    fun updatePollingStationDetails(pollingStation: PollingStation)

    @Query("SELECT * FROM polling_station WHERE countyCode=:countyCode AND idPollingStation=:pollingStationNumber")
    fun get(countyCode: String, pollingStationNumber: Int): Maybe<PollingStation>

    @Query("SELECT county.name AS countyName, polling_station.idPollingStation as pollingStationNumber, county.diaspora as isDiaspora FROM polling_station INNER JOIN county on county.code=polling_station.countyCode WHERE countyCode=:countyCode AND pollingStationNumber=:pollingStationNumber")
    fun getPollingStationInfo(
        countyCode: String,
        pollingStationNumber: Int
    ): Maybe<PollingStationInfo>

    @Query("SELECT * FROM polling_station WHERE synced=:synced")
    fun getNotSyncedPollingStations(synced: Boolean = false): Observable<List<PollingStation>>

    @Query("SELECT COUNT(*) FROM polling_station WHERE synced =:synced")
    fun getCountOfNotSyncedPollingStations(synced: Boolean = false): LiveData<Int>
}