package ro.code4.monitorizarevot.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Maybe
import io.reactivex.Observable
import ro.code4.monitorizarevot.data.model.PollingStation
import ro.code4.monitorizarevot.data.pojo.CountyMunicipalityAndPollingStation
import ro.code4.monitorizarevot.data.pojo.PollingStationInfo

@Dao
interface PollingStationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(pollingStation: PollingStation)

    @Update
    fun updatePollingStationDetails(pollingStation: PollingStation)

    @Query("SELECT * FROM polling_station WHERE countyCode=:countyCode AND municipalityCode=:municipalityCode AND pollingStationNumber=:pollingStationNumber")
    fun get(countyCode:String, municipalityCode: String, pollingStationNumber: Int): Maybe<PollingStation>

    @Query("SELECT province.name AS provinceName, county.name AS countyName, municipality.name AS municipalityName, polling_station.pollingStationNumber as pollingStationNumber FROM polling_station" +
            " INNER JOIN province on province.code=polling_station.provinceCode " +
            " INNER JOIN county on county.code=polling_station.countyCode " +
            " INNER JOIN municipality on municipality.code=polling_station.municipalityCode" +
            " WHERE county.code=:countyCode AND municipalityCode=:municipalityCode AND pollingStationNumber=:pollingStationNumber")
    fun getPollingStationInfo(
        countyCode: String,
        municipalityCode: String,
        pollingStationNumber: Int
    ): Maybe<PollingStationInfo>

    @Query("SELECT * FROM polling_station WHERE synced=:synced")
    fun getNotSyncedPollingStations(synced: Boolean = false): Maybe<List<PollingStation>>

    @Query("SELECT COUNT(*) FROM polling_station WHERE synced =:synced")
    fun getCountOfNotSyncedPollingStations(synced: Boolean = false): LiveData<Int>

    @Query("DELETE FROM polling_station")
    fun deleteAll()

    @Query("SELECT * FROM polling_station WHERE observerArrivalTime NOT NULL")
    fun getVisitedPollingStations(): Observable<List<CountyMunicipalityAndPollingStation>>
}