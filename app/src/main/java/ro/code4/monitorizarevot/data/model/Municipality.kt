package ro.code4.monitorizarevot.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Entity(tableName = "municipality",
    indices = [Index(value = ["code"], unique = true), Index(value = ["countyCode"])],
    foreignKeys = [androidx.room.ForeignKey(
        entity = County::class,
        parentColumns = ["code"],
        childColumns = ["countyCode"],
        onDelete = ForeignKey.CASCADE
    )]
)
@Parcel(Parcel.Serialization.FIELD)
class Municipality {

    @PrimaryKey
    @Expose
    var id: Int = 0

    @Expose
    lateinit var code: String

    @Expose
    lateinit var countyCode: String

    @Expose
    lateinit var name: String

    @Expose
    @SerializedName("numberOfPollingStations")
    var limit: Int = 0

    @Expose
    var order: Int = 0

    override fun toString(): String = name

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Municipality
        if (code != other.code) return false
        if (countyCode != other.countyCode) return false
        if (limit != other.limit) return false
        if (order != other.order) return false
        return true
    }

    override fun hashCode(): Int {
        var result = code.hashCode()
        result = 31 * result + countyCode.hashCode()
        result = 31 * result + limit
        result = 31 * result + order
        return result
    }
}
