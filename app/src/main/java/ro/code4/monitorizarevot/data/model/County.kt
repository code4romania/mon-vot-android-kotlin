package ro.code4.monitorizarevot.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Entity(tableName = "county", indices = [Index(value = ["code"], unique = true)])
@Parcel(Parcel.Serialization.FIELD)
class County {

    @PrimaryKey
    @Expose
    @SerializedName(ID_FIELD)
    var id: Int = 0

    @Expose
    @SerializedName(COUNTY_CODE_FIELD)
    lateinit var code: String

    @Expose
    @SerializedName(COUNTY_NAME_FIELD)
    lateinit var name: String

    @Expose
    @SerializedName(BRANCHES_COUNT_FIELD)
    var branchesCount: Int = 0

    companion object {

        const val ID_FIELD = "id"
        const val COUNTY_CODE_FIELD = "code"
        const val COUNTY_NAME_FIELD = "name"
        const val BRANCHES_COUNT_FIELD = "limit"
    }

    override fun toString(): String = name ?: ""

    override fun equals(other: Any?): Boolean {
        if (other !is County) {
            return false
        }
        return code == other.code
    }
}
