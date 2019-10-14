package ro.code4.monitorizarevot.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Maybe
import ro.code4.monitorizarevot.data.model.BranchDetails
import ro.code4.monitorizarevot.data.pojo.BranchDetailsInfo

@Dao
interface BranchDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(branchDetails: BranchDetails): Completable

    @Query("SELECT * FROM branch_details WHERE countyCode=:countyCode AND branchNumber=:branchNumber")
    fun get(countyCode: String, branchNumber: Int): Maybe<BranchDetails>

    @Query("SELECT county.name AS countyName, branch_details.branchNumber as branchNumber FROM branch_details INNER JOIN county on county.code=branch_details.countyCode WHERE countyCode=:countyCode AND branchNumber=:branchNumber")
    fun getBranchInfo(countyCode: String, branchNumber: Int): Maybe<BranchDetailsInfo>
}