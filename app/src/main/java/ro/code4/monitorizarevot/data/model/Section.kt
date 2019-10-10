package ro.code4.monitorizarevot.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Entity(
    tableName = "section", foreignKeys = [ForeignKey(
        entity = FormDetails::class,
        parentColumns = ["code"],
        childColumns = ["formCode"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
@Parcel(Parcel.Serialization.FIELD)
class Section {

    // TODO serialized names to be translated when api is updated
    @PrimaryKey
    @Expose
    @SerializedName("idSectiune")
    lateinit var id: String

    @Expose
    @SerializedName("codSectiune")
    var code: String? = null

    @Expose
    @SerializedName("descriere")
    var description: String? = null

    @Expose
    @SerializedName("intrebari")
//    @Relation(parentColumn = "id", entityColumn = "section_id")
    @Ignore
    lateinit var questions: List<Question>

    lateinit var formCode: String
    override fun equals(other: Any?): Boolean {
        if (other !is Section) {
            return false
        }

        return id == other.id && code == other.code && description == other.description
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (code?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        return result
    }


}