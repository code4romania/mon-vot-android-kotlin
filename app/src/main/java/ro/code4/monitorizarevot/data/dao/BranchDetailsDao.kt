package ro.code4.monitorizarevot.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Maybe
import ro.code4.monitorizarevot.data.model.BranchDetails

@Dao
interface BranchDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(branchDetails: BranchDetails): Completable

    @Query("SELECT * FROM branch_details WHERE countyCode=:countyCode AND branchNumber=:branchNumber")
    fun get(countyCode: String, branchNumber: Int): Maybe<BranchDetails>
}