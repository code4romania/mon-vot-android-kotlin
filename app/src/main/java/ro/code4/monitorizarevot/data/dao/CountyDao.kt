package ro.code4.monitorizarevot.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import io.reactivex.Maybe
import io.reactivex.Observable
import ro.code4.monitorizarevot.data.model.County

@Dao
interface CountyDao {
    @Query("SELECT * FROM county where provinceCode=:provinceCode")
    fun getByProvinceCode(provinceCode: String): Observable<List<County>>

    @Insert(onConflict = REPLACE)
    fun save(vararg counties: County)

    @Query("SELECT * FROM county where code=:countyCode")
    fun get(countyCode: String): Maybe<County>

    @Query("DELETE FROM county where provinceCode=:provinceCode")
    fun deleteAll(provinceCode: String)
}