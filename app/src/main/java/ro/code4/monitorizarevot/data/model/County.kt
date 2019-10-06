package ro.code4.monitorizarevot.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import org.parceler.Parcel

@Entity(tableName = "county", indices = [Index(value = ["code"], unique = true)])
@Parcel(Parcel.Serialization.FIELD)
class County {

    @PrimaryKey
    @Expose
    var id: Int = 0

    @Expose
    lateinit var code: String

    @Expose
    var name: String? = null

    @Expose
    var limit: Int = 0

    override fun toString(): String = name ?: ""

    override fun equals(other: Any?): Boolean {
        if (other !is County) {
            return false
        }
        return code == other.code
    }
}
