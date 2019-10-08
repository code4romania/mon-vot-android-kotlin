package ro.code4.monitorizarevot.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Entity(
    tableName = "question",
    foreignKeys = [ForeignKey(
        entity = Section::class,
        parentColumns = ["id"],
        childColumns = ["sectionId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
@Parcel(Parcel.Serialization.FIELD)
class Question {

    @PrimaryKey
    @Expose
    @SerializedName("idIntrebare")
    var id: Int = -1

    @Expose
    @SerializedName("textIntrebare")
    lateinit var text: String

    @Expose
    @SerializedName("codIntrebare")
    lateinit var code: String

    @Expose
    @SerializedName("idTipIntrebare") //1  single choice, 2 raspuns cu text, 3 multiple choice si raspuns liber 0 multiple choice
    var questionType: Int = 0

    @Expose
    @SerializedName("raspunsuriDisponibile")
    @Ignore
    lateinit var answers: List<Answer>

    lateinit var sectionId: String

    @Ignore
    var savedLocally = false
    @Ignore
    var synced = false

    override fun equals(other: Any?): Boolean =
        other is Question && id == other.id && text == other.text && code == other.code &&
                questionType == other.questionType &&
                sectionId == other.sectionId && savedLocally == other.savedLocally &&
                synced == other.synced


    override fun hashCode(): Int {
        var result = id
        result = 31 * result + text.hashCode()
        result = 31 * result + code.hashCode()
        result = 31 * result + questionType
        result = 31 * result + sectionId.hashCode()
        result = 31 * result + savedLocally.hashCode()
        result = 31 * result + synced.hashCode()
        return result
    }


}