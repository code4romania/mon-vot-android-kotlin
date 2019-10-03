package ro.code4.monitorizarevot.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Maybe
import ro.code4.monitorizarevot.data.model.Note

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg note: Note): Maybe<Int>

    @Update
    fun updateNote(vararg note: Note): Completable

    @Query("SELECT * FROM note WHERE countyCode=:countyCode AND branchNumber=:branchNumber AND questionId=:questionId ORDER BY date DESC")
    fun getNotesForQuestion(
        countyCode: String,
        branchNumber: Int,
        questionId: Int? = null
    ): LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE countyCode=:countyCode AND branchNumber=:branchNumber ORDER BY date DESC")
    fun getNotes(countyCode: String, branchNumber: Int): LiveData<List<Note>>
}