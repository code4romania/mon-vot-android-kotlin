package ro.code4.monitorizarevot.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
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
    @SerializedName("idOptiune")
    var id: Int = -1

    @Expose
    @SerializedName("textOptiune")
    lateinit var text: String

    @Expose
    @SerializedName("seIntroduceText")
    var hasManualInput: Boolean = false

    var questionId: Int = -1


    @Ignore
    var selected = false
    @Ignore
    var value: String? = null
}