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

    override fun equals(other: Any?): Boolean =
        other is Answer && id == other.id && text == other.text &&
                hasManualInput == other.hasManualInput && questionId == other.questionId &&
                selected == other.selected && value == other.value

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + text.hashCode()
        result = 31 * result + hasManualInput.hashCode()
        result = 31 * result + questionId
        result = 31 * result + selected.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        return result
    }
}