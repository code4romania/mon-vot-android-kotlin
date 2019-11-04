package ro.code4.monitorizarevot.data.model.answers

import androidx.room.Entity
import androidx.room.ForeignKey
import com.google.gson.annotations.Expose
import ro.code4.monitorizarevot.data.model.Answer

@Entity(
    tableName = "selected_answer", foreignKeys = [ForeignKey(
        entity = Answer::class,
        parentColumns = ["idOption"],
        childColumns = ["optionId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = AnsweredQuestion::class,
        parentColumns = ["countyCode", "pollingStationNumber", "id"],
        childColumns = ["countyCode", "pollingStationNumber", "questionId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    primaryKeys = ["optionId", "countyCode", "pollingStationNumber"]
)
class SelectedAnswer() {

    @Expose
    var optionId: Int = -1

    @Expose
    var value: String? = null

    lateinit var countyCode: String

    var pollingStationNumber: Int = 0

    lateinit var questionId: String

    constructor(
        optionId: Int,
        countyCode: String,
        pollingStationNumber: Int,
        questionId: String,
        value: String? = null
    ) : this() {
        this.optionId = optionId
        this.countyCode = countyCode
        this.pollingStationNumber = pollingStationNumber
        this.value = value
        this.questionId = questionId
    }

}
