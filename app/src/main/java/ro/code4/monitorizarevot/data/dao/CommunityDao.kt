package ro.code4.monitorizarevot.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import io.reactivex.Maybe
import io.reactivex.Observable
import ro.code4.monitorizarevot.data.model.Community

@Dao
interface CommunityDao {
    @Query("SELECT * FROM community where countyCode=:countyCode")
    fun getAll(countyCode: String): Observable<List<Community>>

    @Insert(onConflict = REPLACE)
    fun save(vararg communities: Community)

    @Query("SELECT * FROM community where code=:communityCode")
    fun get(communityCode: String): Maybe<Community>

    @Query("DELETE FROM community where countyCode=:countyCode")
    fun deleteAll(countyCode: String)
}