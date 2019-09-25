package ro.code4.monitorizarevot.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Entity(tableName = "form_details")
@Parcel(Parcel.Serialization.FIELD)
class FormDetails {
    @PrimaryKey
    @Expose
    @SerializedName("code")
    lateinit var code: String

    @Expose
    @SerializedName("description")
    lateinit var description: String

    @Expose
    @SerializedName("ver")
    var formVersion: Int = 0

//    @Relation(parentColumn = "code", entityColumn = "form_code")
//    lateinit var sections: List<Section>

    override fun equals(other: Any?): Boolean {
        if (other !is FormDetails) {
            return false
        }
        return code == other.code && formVersion == other.formVersion

    }

    override fun hashCode(): Int {
        var result = code.hashCode()
        result = 31 * result + formVersion
        return result
    }
}