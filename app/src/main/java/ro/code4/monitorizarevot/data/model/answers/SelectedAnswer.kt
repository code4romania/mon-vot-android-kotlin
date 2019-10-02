package ro.code4.monitorizarevot.data.model.answers

import androidx.room.Entity
import androidx.room.ForeignKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ro.code4.monitorizarevot.data.model.Answer

@Entity(
    tableName = "selected_answer", foreignKeys = [ForeignKey(
        entity = Answer::class,
        parentColumns = ["id"],
        childColumns = ["optionId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = AnsweredQuestion::class,
        parentColumns = ["countyCode", "sectionNumber", "id"],
        childColumns = ["countyCode", "branchNumber", "questionId"],
        onDelete = ForeignKey.CASCADE
    )],
    primaryKeys = ["optionId", "countyCode", "branchNumber"]
)
class SelectedAnswer() {

    //TODO update fields to English names - to check names if influenced by backend

    @Expose
    @SerializedName("idOptiune")
    var optionId: Int = -1

    @Expose
    @SerializedName("value")
    var value: String? = null

    //    @SerializedName("codJudet")
    lateinit var countyCode: String

    //    @SerializedName("numarSectie")
    var branchNumber: Int = 0

    lateinit var questionId: String

    constructor(
        optionId: Int,
        countyCode: String,
        branchNumber: Int,
        questionId: String,
        value: String? = null
    ) : this() {
        this.optionId = optionId
        this.countyCode = countyCode
        this.branchNumber = branchNumber
        this.value = value
        this.questionId = questionId
    }

}
