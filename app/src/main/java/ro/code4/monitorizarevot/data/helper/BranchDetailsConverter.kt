package ro.code4.monitorizarevot.data.helper

import androidx.room.TypeConverter
import com.google.gson.Gson
import org.koin.core.KoinComponent
import org.koin.core.inject
import ro.code4.monitorizarevot.data.model.BranchDetails

object BranchDetailsConverter : KoinComponent {
    private val gson: Gson by inject()

    @TypeConverter
    fun toString(branchDetails: BranchDetails?): String? = gson.toJson(branchDetails)

    @TypeConverter
    fun toBranchDetails(value: String): BranchDetails? {
        return gson.fromJson(value, BranchDetails::class.java)
    }
}