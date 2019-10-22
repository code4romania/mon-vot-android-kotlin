package ro.code4.monitorizarevot.data.model.answers

import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ro.code4.monitorizarevot.data.model.BranchDetails
import ro.code4.monitorizarevot.data.model.FormDetails
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
            entity = BranchDetails::class,
            parentColumns = ["countyCode", "branchNumber"],
            childColumns = ["countyCode", "pollingStationNumber"]
        )], indices = [Index(value = ["countyCode", "pollingStationNumber", "id"], unique = true)]
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
    var pollingStationNumber: Int = -1

    @Ignore
    @Expose
    lateinit var options: List<SelectedAnswer>

    var savedLocally = false
    var synced = false

    constructor(
        questionId: Int,
        countyCode: String,
        branchNumber: Int,
        formId: Int
    ) : this() {
        this.id = "$countyCode$branchNumber$formId$questionId"
        this.questionId = questionId
        this.countyCode = countyCode
        this.pollingStationNumber = branchNumber
        this.formId = formId
    }

}
