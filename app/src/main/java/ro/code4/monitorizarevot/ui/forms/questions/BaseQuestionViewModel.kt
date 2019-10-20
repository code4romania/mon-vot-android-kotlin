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

    var selectedFormId: Int = -1

    fun questions(): LiveData<ArrayList<ListItem>> = questionsLiveData

    private fun getQuestions(formId: Int) {

        selectedFormId = formId
        zipLiveData(
            repository.getSectionsWithQuestions(formId),
            repository.getAnswersForForm(countyCode, branchNumber, formId)
        ).observeForever {
            processList(it.first, it.second)

        }

    }

    abstract fun processList(
        sections: List<SectionWithQuestions>,
        answersForForm: List<AnsweredQuestionPOJO>
    )

    fun setData(formDetails: FormDetails) {
        getQuestions(formDetails.id)
        setTitle(formDetails.description)
    }

}