package ro.code4.monitorizarevot.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Entity(tableName = "form_details")
@Parcel(Parcel.Serialization.FIELD)
class FormDetails {
    @PrimaryKey
    @Expose
    var id: Int = -1

    @Expose
    lateinit var code: String

    @Expose
    lateinit var description: String

    @Expose
    @SerializedName("ver")
    var formVersion: Int = 0

    @Ignore
    lateinit var sections: List<Section>

    override fun equals(other: Any?): Boolean =
        other is FormDetails && id == other.id && code == other.code && formVersion == other.formVersion

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + code.hashCode()
        result = 31 * result + formVersion
        return result
    }


}