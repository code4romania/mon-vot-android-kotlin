package ro.code4.monitorizarevot.data.model.answers

import androidx.room.*
import com.google.gson.annotations.Expose
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.model.PollingStation
import ro.code4.monitorizarevot.data.model.Question

@Entity(
    tableName = "answered_question", foreignKeys = [
        ForeignKey(
            entity = FormDetails::class,
            parentColumns = ["id"],
            childColumns = ["formId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Question::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PollingStation::class,
            parentColumns = ["countyCode", "communityCode", "pollingStationNumber"],
            childColumns = ["countyCode", "communityCode", "pollingStationNumber"]
        )], indices = [Index(value = ["countyCode", "communityCode", "pollingStationNumber", "id"], unique = true)]
)
class AnsweredQuestion() {
    @PrimaryKey
    lateinit var id: String

    @Expose
    var formId: Int = -1

    @Expose
    var questionId: Int = -1

    @Expose
    lateinit var countyCode: String

    @Expose
    lateinit var communityCode: String

    @Expose
    var pollingStationNumber: Int = -1

    @Ignore
    @Expose
    lateinit var options: List<SelectedAnswer>

    var savedLocally = false
    var synced = false

    constructor(
        questionId: Int,
        countyCode: String,
        communityCode: String,
        pollingStationNumber: Int,
        formId: Int
    ) : this() {
        this.id = "${countyCode}_${communityCode}_${pollingStationNumber}_${formId}_$questionId"
        this.questionId = questionId
        this.countyCode = countyCode
        this.communityCode = communityCode
        this.pollingStationNumber = pollingStationNumber
        this.formId = formId
    }

}
