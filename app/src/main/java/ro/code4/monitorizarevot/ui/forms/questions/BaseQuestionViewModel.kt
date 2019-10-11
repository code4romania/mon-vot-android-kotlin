package ro.code4.monitorizarevot.ui.forms.questions

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.koin.core.inject
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.pojo.AnsweredQuestionPOJO
import ro.code4.monitorizarevot.data.pojo.SectionWithQuestions
import ro.code4.monitorizarevot.helper.getBranchNumber
import ro.code4.monitorizarevot.helper.getCountyCode
import ro.code4.monitorizarevot.helper.zipLiveData
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel

abstract class BaseQuestionViewModel : BaseViewModel() {
    val repository: Repository by inject()
    private val preferences: SharedPreferences by inject()
    val questionsLiveData = MutableLiveData<ArrayList<ListItem>>()
    private val titleLiveData = MutableLiveData<String>()

    lateinit var selectedFormCode: String
    var countyCode: String
    var branchNumber: Int = -1

    fun title(): LiveData<String> = titleLiveData
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

    abstract fun processList(
        sections: List<SectionWithQuestions>,
        answersForForm: List<AnsweredQuestionPOJO>
    )

    fun setData(formDetails: FormDetails) {
        getQuestions(formDetails.code)
        titleLiveData.postValue(formDetails.description)
    }

}