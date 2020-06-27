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
    var id: Int = 0

    @Expose
    lateinit var code: String

    @Expose
    lateinit var name: String

    @Expose
    @SerializedName("numberOfPollingStations")
    var limit: Int = 0

    @Expose
    var diaspora: Boolean? = null

    @Expose
    var order: Int = 0

    override fun toString(): String = name

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as County
        if (code != other.code) return false
        if (limit != other.limit) return false
        if (diaspora != other.diaspora) return false
        if (order != other.order) return false
        return true
    }

    override fun hashCode(): Int {
        var result = code.hashCode()
        result = 31 * result + limit
        result = 31 * result + (diaspora?.hashCode() ?: 0)
        result = 31 * result + order
        return result
    }
}
