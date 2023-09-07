package ro.code4.monitorizarevot.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.parceler.Parcel
import java.util.*

@Entity(
    tableName = "note", foreignKeys = [ForeignKey(
        entity = Question::class,
        parentColumns = ["id"],
        childColumns = ["questionId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = PollingStation::class,
        parentColumns = ["countyCode", "communityCode", "pollingStationNumber"],
        childColumns = ["countyCode", "communityCode", "pollingStationNumber"]
    )],
    indices = [Index(value = ["countyCode", "communityCode", "pollingStationNumber", "questionId"], unique = false)]
)
@Parcel(Parcel.Serialization.FIELD)
class Note {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var uriPath: String? = null

    lateinit var description: String

    var questionId: Int? = null

    var date: Date = Date()

    lateinit var communityCode: String
    lateinit var countyCode: String

    var pollingStationNumber = 0

    var synced = false

    var formCode: String? = null

    var questionCode: String? = null

    override fun equals(other: Any?): Boolean =
        other is Note
                && other.id == id
                && other.uriPath == uriPath
                && other.questionId == questionId
                && other.date == date
                && other.pollingStationNumber == pollingStationNumber
                && other.synced == synced

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (uriPath?.hashCode() ?: 0)
        result = 31 * result + (questionId ?: 0)
        result = 31 * result + date.hashCode()
        result = 31 * result + pollingStationNumber
        result = 31 * result + synced.hashCode()
        return result
    }
}