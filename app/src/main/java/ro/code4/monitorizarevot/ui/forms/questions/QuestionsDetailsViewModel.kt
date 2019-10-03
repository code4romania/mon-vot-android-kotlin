package ro.code4.monitorizarevot.ui.forms.questions

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.koin.core.inject
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.model.answers.AnsweredQuestion
import ro.code4.monitorizarevot.data.model.answers.SelectedAnswer
import ro.code4.monitorizarevot.data.pojo.AnsweredQuestionPOJO
import ro.code4.monitorizarevot.data.pojo.QuestionWithAnswers
import ro.code4.monitorizarevot.data.pojo.SectionWithQuestions
import ro.code4.monitorizarevot.helper.getBranchNumber
import ro.code4.monitorizarevot.helper.getCountyCode
import ro.code4.monitorizarevot.helper.zipLiveData
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class QuestionsDetailsViewModel : BaseViewModel() {
    private val repository: Repository by inject()
    private val preferences: SharedPreferences by inject()
    private val questionsLiveData = MutableLiveData<ArrayList<QuestionWithAnswers>>()
    private lateinit var selectedFormCode: String
    private var countyCode: String
    private var branchNumber: Int = -1

    fun questions(): LiveData<ArrayList<QuestionWithAnswers>> = questionsLiveData

    init {
        countyCode = preferences.getCountyCode()!!
        branchNumber = preferences.getBranchNumber()
    }

    private fun getQuestions(formCode: String) {

        selectedFormCode = formCode
        zipLiveData(
            repository.getSectionsWithQuestions(formCode),
            repository.getAnswersForForm(countyCode, branchNumber, formCode)
        ).observeForever {
            processList(it.first, it.second)

        }

    }

    private fun processList(
        sections: List<SectionWithQuestions>,
        answersForForm: List<AnsweredQuestionPOJO>
    ) {
        val list = ArrayList<QuestionWithAnswers>()
        sections.forEach { sectionWithQuestion ->
            sectionWithQuestion.questions.forEach { questionWithAnswers ->
                questionWithAnswers.answers.forEach { answer ->
                    val answeredQuestion =
                        answersForForm.find { it.answeredQuestion.questionId == questionWithAnswers.question.id }
                    answeredQuestion?.also { savedQuestion ->
                        val selectedAnswer =
                            savedQuestion.selectedAnswers.find { it.optionId == answer.id }
                        questionWithAnswers.question.savedLocally =
                            savedQuestion.answeredQuestion.savedLocally
                        questionWithAnswers.question.synced = savedQuestion.answeredQuestion.synced
                        if (selectedAnswer != null) {
                            answer.selected = true
                            if (answer.hasManualInput) {
                                answer.value = selectedAnswer.value ?: ""
                            }
                        }
                    }
                }
            }
            list.addAll(sectionWithQuestion.questions)
        }
        questionsLiveData.postValue(list)
    }

    fun setData(formDetails: FormDetails) {
        getQuestions(formDetails.code)
    }

    fun saveAnswer(questionWithAnswers: QuestionWithAnswers) {
        val answers = questionWithAnswers.answers.filter { it.selected }
        if (answers.isNotEmpty()) {
            val answeredQuestion = AnsweredQuestion(
                questionWithAnswers.question.id,
                countyCode,
                branchNumber,
                selectedFormCode
            )
            val list = answers.map {
                SelectedAnswer(
                    it.id,
                    countyCode,
                    branchNumber,
                    answeredQuestion.id,
                    if (it.hasManualInput) it.value else null
                )
            }
            repository.saveAnsweredQuestion(answeredQuestion, list)
        }
    }

    fun syncData() {
        repository.syncAnswers(countyCode, branchNumber, selectedFormCode)
    }

}