package ro.code4.monitorizarevot.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import io.reactivex.Maybe
import ro.code4.monitorizarevot.data.model.County

@Dao
interface CountyDao {
    @Query("SELECT * FROM county")
    fun getAll(): Maybe<List<County>>

    @Insert(onConflict = REPLACE)
    fun save(vararg counties: County)

}