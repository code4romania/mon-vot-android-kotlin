package ro.code4.monitorizarevot.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import io.reactivex.Maybe
import io.reactivex.Observable
import ro.code4.monitorizarevot.data.model.Province

@Dao
interface ProvinceDao {
    @Query("SELECT * FROM province")
    fun getAll(): Observable<List<Province>>

    @Insert(onConflict = REPLACE)
    fun save(vararg counties: Province)

    @Query("SELECT * FROM Province where code=:provinceCode")
    fun get(provinceCode: String): Maybe<Province>

    @Query("DELETE FROM province")
    fun deleteAll()
}