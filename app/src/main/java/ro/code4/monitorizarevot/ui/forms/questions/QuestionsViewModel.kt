package ro.code4.monitorizarevot.ui.forms.questions

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.koin.core.inject
import ro.code4.monitorizarevot.adapters.QuestionsAdapter.Companion.TYPE_QUESTION
import ro.code4.monitorizarevot.adapters.QuestionsAdapter.Companion.TYPE_SECTION
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.pojo.AnsweredQuestionPOJO
import ro.code4.monitorizarevot.data.pojo.SectionWithQuestions
import ro.code4.monitorizarevot.helper.getBranchNumber
import ro.code4.monitorizarevot.helper.getCountyCode
import ro.code4.monitorizarevot.helper.zipLiveData
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class QuestionsViewModel : BaseViewModel() {
    private val repository: Repository by inject()
    private val preferences: SharedPreferences by inject()
    private val questionsLiveData = MutableLiveData<ArrayList<ListItem>>()
    private lateinit var selectedFormCode: String
    private var countyCode: String
    private var branchNumber: Int = -1

    fun questions(): LiveData<ArrayList<ListItem>> = questionsLiveData

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
        val list = ArrayList<ListItem>()
        sections.forEachIndexed { index, sectionWithQuestion ->
            list.add(ListItem(TYPE_SECTION, Pair(index + 1, sectionWithQuestion.section)))
            list.addAll(sectionWithQuestion.questions.map { questionWithAnswers ->
                val answeredQuestion =
                    answersForForm.find { it.answeredQuestion.questionId == questionWithAnswers.question.id }
                answeredQuestion?.let {
                    questionWithAnswers.question.synced = it.answeredQuestion.synced
                    questionWithAnswers.question.savedLocally = it.answeredQuestion.savedLocally
                }
                ListItem(TYPE_QUESTION, questionWithAnswers)
            })
        }
        questionsLiveData.postValue(list)
    }

    fun setData(formDetails: FormDetails) {
        getQuestions(formDetails.code)
    }

}