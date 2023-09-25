package ro.code4.monitorizarevot.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import org.parceler.Parcel

@Entity(tableName = "province", indices = [Index(value = ["code"], unique = true)])
@Parcel(Parcel.Serialization.FIELD)
class Province() {

    @PrimaryKey
    @Expose
    var id: Int = 0

    @Expose
    lateinit var code: String

    @Expose
    lateinit var name: String

    @Expose
    var order: Int = -1

    constructor(id: Int, code: String, name: String, order: Int): this() {
        this.id = id
        this.code = code
        this.name = name
        this.order = order
    }

    override fun toString(): String = name

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Province
        if (code != other.code) return false
        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}