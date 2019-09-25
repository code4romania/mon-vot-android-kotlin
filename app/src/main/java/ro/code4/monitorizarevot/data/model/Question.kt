package ro.code4.monitorizarevot.data.model

import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Entity(
    tableName = "question",
    foreignKeys = [ForeignKey(
        entity = Section::class,
        parentColumns = ["id"],
        childColumns = ["section_id"],
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
    var typeId: Int = 1

    @Expose
    @SerializedName("raspunsuriDisponibile")
    @Ignore
//    @Relation(parentColumn = "id", entityColumn = "question_id")
    lateinit var answers: List<Answer>

    @ColumnInfo(name = "section_id")
    lateinit var sectionId: String
}