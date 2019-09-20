package ro.code4.monitorizarevot.data.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Maybe

import ro.code4.monitorizarevot.data.model.FormDetails

@Dao
interface FormsDao {
    @Query("SELECT * FROM form_details")
    fun getAll(): Maybe<List<FormDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg forms: FormDetails): Completable

    @Delete
    fun deleteForms(vararg forms: FormDetails): Completable
}