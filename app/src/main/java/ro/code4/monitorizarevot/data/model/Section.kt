package ro.code4.monitorizarevot.data.model

import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Entity(
    tableName = "section", foreignKeys = [ForeignKey(
        entity = FormDetails::class,
        parentColumns = ["code"],
        childColumns = ["form_code"],
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

    @ColumnInfo(name = "form_code")
    lateinit var formCode: String
}