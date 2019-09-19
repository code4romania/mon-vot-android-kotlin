package ro.code4.monitorizarevot.data.model

import org.parceler.Parcel
import ro.code4.monitorizarevot.data.model.response.ResponseAnswer

//@Entity(tableName = "branch_question_answer")
@Parcel(Parcel.Serialization.FIELD)
class BranchQuestionAnswer {

    //        @PrimaryKey
    var id: String? = null

    var countryCode: String? = null
    var branchNumber: Int = 0
    var questionId: Int = 0
    var branchDetails: BranchDetails? = null
    val answers = ArrayList<ResponseAnswer>()

    constructor() {

    }

    constructor(countryCode: String, branchNumber: Int) {
        this.countryCode = countryCode
        this.branchNumber = branchNumber
    }

    constructor(questionId: Int?) : this(
//        Preferences.getCountyCode(), //TODO
//        Preferences.getBranchNumber()
    ) {
        this.questionId = questionId!!
    }

    constructor(questionId: Int?, answers: List<ResponseAnswer>) : this(questionId) {
        this.answers.clear()
        this.answers.addAll(answers)
        this.id = countryCode + branchNumber.toString() + questionId.toString()
    }

    fun setAnswers(answers: List<ResponseAnswer>) {
        this.answers.clear()
        this.answers.addAll(answers)
    }


}
