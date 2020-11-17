package ro.code4.monitorizarevot.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import org.parceler.Parcel

@Entity(
    tableName = "answer", foreignKeys = [ForeignKey(
        entity = Question::class,
        parentColumns = ["id"],
        childColumns = ["questionId"],
        onDelete = ForeignKey.CASCADE
    )]
)
@Parcel(Parcel.Serialization.FIELD)
class Answer {
    @PrimaryKey
    @Expose
    var idOption: Int = -1

    @Expose
    lateinit var text: String

    @Expose
    var isFreeText: Boolean = false

    var questionId: Int = -1


    @Ignore
    var selected = false
    @Ignore
    var value: String? = null

    @Expose
    var orderNumber = 0

    override fun equals(other: Any?): Boolean =
        other is Answer && idOption == other.idOption && text == other.text &&
                isFreeText == other.isFreeText && questionId == other.questionId &&
                selected == other.selected && value == other.value

    override fun hashCode(): Int {
        var result = idOption
        result = 31 * result + text.hashCode()
        result = 31 * result + isFreeText.hashCode()
        result = 31 * result + questionId
        result = 31 * result + selected.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        return result
    }
}