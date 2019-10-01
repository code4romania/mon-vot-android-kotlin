package ro.code4.monitorizarevot.ui.forms.questions

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.koin.core.inject
import ro.code4.monitorizarevot.data.model.FormDetails
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

    fun questions(): LiveData<ArrayList<QuestionWithAnswers>> = questionsLiveData

    private fun getQuestions(formCode: String) {

        selectedFormCode = formCode
        zipLiveData(
            repository.getSectionsWithQuestions(formCode),
            repository.getAnswersForForm(
                preferences.getCountyCode(),
                preferences.getBranchNumber(),
                formCode
            )
        )
            .observeForever {
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
                    val selectedAnswer =
                        answersForForm.find { it.answeredQuestion.questionId == questionWithAnswers.question.id }
                            ?.selectedAnswers?.find { it.optionId == answer.id }
                    if (selectedAnswer != null) {
                        answer.selected = true
                        if (answer.hasManualInput) {
                            answer.text = selectedAnswer.value ?: ""
                        }
                    }
                }
            }
            list.addAll(sectionWithQuestion.questions)
        }
//        sections?.forEach { sectionWithQuestion ->
//            sectionWithQuestion.questions.map { it.answers.map { answer -> if(answersForForm.find{it.a}) } }
//            list.addAll(sectionWithQuestion.questions)
//        }
        questionsLiveData.postValue(list)
    }

    fun setData(formDetails: FormDetails) {
        getQuestions(formDetails.code)
    }

}