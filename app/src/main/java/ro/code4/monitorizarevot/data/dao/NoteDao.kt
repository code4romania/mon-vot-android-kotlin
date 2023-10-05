package ro.code4.monitorizarevot.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Maybe
import io.reactivex.Observable
import ro.code4.monitorizarevot.data.model.Note

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg note: Note): List<Long>

    @Update
    fun updateNote(vararg note: Note)

    @Query("SELECT * FROM note WHERE countyCode=:countyCode AND municipalityCode=:municipalityCode AND pollingStationNumber=:pollingStationNumber AND questionId=:questionId ORDER BY date DESC")
    fun getNotesForQuestion(
        countyCode: String,
        municipalityCode: String,
        pollingStationNumber: Int,
        questionId: Int? = null
    ): LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE countyCode=:countyCode AND municipalityCode=:municipalityCode AND pollingStationNumber=:pollingStationNumber AND questionId=:questionId ORDER BY date DESC")
    fun getNotesForQuestionAsObservable(
        countyCode: String,
        municipalityCode: String,
        pollingStationNumber: Int,
        questionId: Int? = null
    ): Observable<List<Note>>

    @Query("SELECT * FROM note WHERE countyCode=:countyCode AND municipalityCode=:municipalityCode AND pollingStationNumber=:pollingStationNumber ORDER BY date DESC")
    fun getNotes(countyCode:String, municipalityCode: String, pollingStationNumber: Int): LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE countyCode=:countyCode AND municipalityCode=:municipalityCode AND pollingStationNumber=:pollingStationNumber ORDER BY date DESC")
    fun getNotesAsObservable(countyCode:String, municipalityCode: String, pollingStationNumber: Int): Observable<List<Note>>

    @Query("SELECT * FROM note WHERE synced=:synced")
    fun getNotSyncedNotes(synced: Boolean = false): Maybe<List<Note>>

    @Query("SELECT COUNT(*) FROM note WHERE synced =:synced")
    fun getCountOfNotSyncedNotes(synced: Boolean = false): LiveData<Int>

    @Query("DELETE FROM note")
    fun deleteAll()
}