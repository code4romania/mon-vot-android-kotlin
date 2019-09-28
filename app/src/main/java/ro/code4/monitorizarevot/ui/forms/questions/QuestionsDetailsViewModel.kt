package ro.code4.monitorizarevot.ui.forms.questions

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.koin.core.inject
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.pojo.QuestionWithAnswers
import ro.code4.monitorizarevot.data.pojo.SectionWithQuestions
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
        repository.getSectionsWithQuestions(formCode).observeForever {
            processList(it)
        }

    }

    private fun processList(sections: List<SectionWithQuestions>) {
        val list = ArrayList<QuestionWithAnswers>()
        sections.forEach { sectionWithQuestion ->
            list.addAll(sectionWithQuestion.questions)
        }
        questionsLiveData.postValue(list)
    }

    fun setData(formDetails: FormDetails) {
        getQuestions(formDetails.code)
    }

}