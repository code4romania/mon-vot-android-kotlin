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
//    @Relation(parentColumn = "id", entityColumn = "question_id")
    lateinit var answers: List<Answer>

    lateinit var sectionId: String

    @Ignore
    var savedLocally = false
    @Ignore
    var synced = false
}