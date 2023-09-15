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
        parentColumns = ["countyCode", "municipalityCode", "pollingStationNumber", "id"],
        childColumns = ["countyCode", "municipalityCode", "pollingStationNumber", "questionId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    primaryKeys = ["optionId", "countyCode", "municipalityCode", "pollingStationNumber"]
)
class SelectedAnswer() {

    @Expose
    var optionId: Int = -1

    @Expose
    var value: String? = null

    lateinit var countyCode: String
    lateinit var municipalityCode: String

    var pollingStationNumber: Int = 0

    lateinit var questionId: String

    constructor(
        optionId: Int,
        countyCode: String,
        municipalityCode: String,
        pollingStationNumber: Int,
        questionId: String,
        value: String? = null
    ) : this() {
        this.optionId = optionId
        this.countyCode = countyCode
        this.municipalityCode = municipalityCode
        this.pollingStationNumber = pollingStationNumber
        this.value = value
        this.questionId = questionId
    }

}
