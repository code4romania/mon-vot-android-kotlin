package ro.code4.monitorizarevot.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Maybe
import io.reactivex.Observable
import ro.code4.monitorizarevot.data.model.BranchDetails
import ro.code4.monitorizarevot.data.pojo.BranchDetailsInfo

@Dao
interface BranchDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(branchDetails: BranchDetails)

    @Update
    fun updateBranchDetails(branchDetails: BranchDetails)

    @Query("SELECT * FROM branch_details WHERE countyCode=:countyCode AND branchNumber=:branchNumber")
    fun get(countyCode: String, branchNumber: Int): Maybe<BranchDetails>

    @Query("SELECT county.name AS countyName, branch_details.branchNumber as branchNumber FROM branch_details INNER JOIN county on county.code=branch_details.countyCode WHERE countyCode=:countyCode AND branchNumber=:branchNumber")
    fun getBranchInfo(countyCode: String, branchNumber: Int): Maybe<BranchDetailsInfo>

    @Query("SELECT * FROM branch_details WHERE synced=:synced")
    fun getNotSyncedBranches(synced: Boolean = false): Observable<List<BranchDetails>>

    @Query("SELECT COUNT(*) FROM branch_details WHERE synced =:synced")
    fun getCountOfNotSyncedBranchDetails(synced: Boolean = false): LiveData<Int>
}