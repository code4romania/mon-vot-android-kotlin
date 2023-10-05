package ro.code4.monitorizarevot.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import io.reactivex.Maybe
import io.reactivex.Observable
import ro.code4.monitorizarevot.data.model.Municipality

@Dao
interface MunicipalityDao {
    @Query("SELECT * FROM municipality")
    fun getAll(): Observable<List<Municipality>>

    @Insert(onConflict = REPLACE)
    fun save(vararg municipalities: Municipality)

    @Query("SELECT * FROM municipality where code=:municipalityCode")
    fun get(municipalityCode: String): Maybe<Municipality>

    @Query("SELECT * FROM municipality where countyCode = :countyCode")
    fun getByCounty(countyCode: String): Observable<List<Municipality>>

    @Query("DELETE FROM municipality where countyCode = :countyCode")
    fun deleteAll(countyCode: String)
}