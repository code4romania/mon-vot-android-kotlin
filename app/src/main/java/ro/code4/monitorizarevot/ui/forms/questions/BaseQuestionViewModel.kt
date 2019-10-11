package ro.code4.monitorizarevot.ui.forms.questions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.pojo.AnsweredQuestionPOJO
import ro.code4.monitorizarevot.data.pojo.SectionWithQuestions
import ro.code4.monitorizarevot.helper.zipLiveData
import ro.code4.monitorizarevot.ui.base.BaseFormViewModel

abstract class BaseQuestionViewModel : BaseFormViewModel() {

    val questionsLiveData = MutableLiveData<ArrayList<ListItem>>()

    lateinit var selectedFormCode: String

    fun questions(): LiveData<ArrayList<ListItem>> = questionsLiveData

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
        setTitle(formDetails.description)
    }

}