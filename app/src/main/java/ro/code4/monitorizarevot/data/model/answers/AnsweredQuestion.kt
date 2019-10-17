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
            parentColumns = ["code"],
            childColumns = ["formCode"],
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
            childColumns = ["countyCode", "pollingStation"]
        )], indices = [Index(value = ["countyCode", "pollingStation", "id"], unique = true)]
)
class AnsweredQuestion() {
    @PrimaryKey
    lateinit var id: String
    @Expose
    @SerializedName("codFormular")
    lateinit var formCode: String
    // TODO serialized names to be translated when api is updated
    @Expose
    var questionId: Int = -1

    @Expose
    lateinit var countyCode: String

    @Expose
    var pollingStation: Int = -1

    @Ignore
    @Expose
    @SerializedName("optiuni")
    lateinit var options: List<SelectedAnswer>

    var savedLocally = false
    var synced = false

    constructor(
        questionId: Int,
        countyCode: String,
        branchNumber: Int,
        formCode: String
    ) : this() {
        this.id = "$countyCode$branchNumber$formCode$questionId"
        this.questionId = questionId
        this.countyCode = countyCode
        this.pollingStation = branchNumber
        this.formCode = formCode
    }

}
