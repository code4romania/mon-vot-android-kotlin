package ro.code4.monitorizarevot.ui.forms

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.monitorizarevot.adapters.helper.AddNoteListItem
import ro.code4.monitorizarevot.adapters.helper.FormListItem
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.data.pojo.AnsweredQuestionPOJO
import ro.code4.monitorizarevot.data.pojo.FormWithSections
import ro.code4.monitorizarevot.helper.getBranchNumber
import ro.code4.monitorizarevot.helper.getCountyCode
import ro.code4.monitorizarevot.helper.zipLiveData
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class FormsViewModel : BaseViewModel() {
    private val repository: Repository by inject()
    private val preferences: SharedPreferences by inject()
    private val formsLiveData = MutableLiveData<ArrayList<ListItem>>()
    private val selectedFormLiveData = MutableLiveData<FormDetails>()
    private val selectedQuestionLiveData = MutableLiveData<Pair<FormDetails, Question>>()
    private val syncVisibilityLiveData = MutableLiveData<Int>()
    private val navigateToNotesLiveData = MutableLiveData<Question?>()
    private val titleLiveData = MutableLiveData<String>()
    private var countyCode: String
    private var branchNumber: Int = -1

    fun title(): LiveData<String> = titleLiveData
    fun setTitle(title: String) = titleLiveData.postValue(title)

    init {

        getForms()
        countyCode = preferences.getCountyCode()!!
        branchNumber = preferences.getBranchNumber()
    }

    private fun subscribe() {
        zipLiveData(
            repository.getNotSyncedQuestions(),
            repository.getNotSyncedNotes()
        ).observeForever {
            syncVisibilityLiveData.postValue(if (it.first + it.second != 0) View.VISIBLE else View.GONE)
        }
        zipLiveData(
            repository.getAnswers(countyCode, branchNumber),
            repository.getFormsWithQuestions()
        ).observeForever {
            processList(it.first, it.second)
        }
    }

    fun forms(): LiveData<ArrayList<ListItem>> {
        subscribe()
        return formsLiveData
    }

    fun selectedForm(): LiveData<FormDetails> = selectedFormLiveData
    fun selectedQuestion(): LiveData<Pair<FormDetails, Question>> = selectedQuestionLiveData
    fun navigateToNotes(): LiveData<Question?> = navigateToNotesLiveData

    private val branchBarTextLiveData = MutableLiveData<String>()


    fun branchBarText(): LiveData<String> = branchBarTextLiveData

    fun getBranchBarText() {
        branchBarTextLiveData.postValue("${preferences.getCountyCode()} ${preferences.getBranchNumber()}") //todo
    }

    @SuppressLint("CheckResult")
    fun getForms() {

        repository.getForms()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {
                onError(it)
            })

    }

    private fun processList(answers: List<AnsweredQuestionPOJO>, forms: List<FormWithSections>) {
        val items = ArrayList<ListItem>()
        forms.forEach { formWithSections ->
            formWithSections.noAnsweredQuestions =
                answers.count { it.answeredQuestion.formCode == formWithSections.form.code }
        }
        items.addAll(forms.map { FormListItem(it) })
        items.add(AddNoteListItem())
        formsLiveData.postValue(items)
    }

    fun selectForm(formDetails: FormDetails) {
        selectedFormLiveData.postValue(formDetails)
    }

    fun selectQuestion(question: Question) {
        selectedQuestionLiveData.postValue(Pair(selectedFormLiveData.value!!, question))
    }

    fun syncVisibility(): LiveData<Int> = syncVisibilityLiveData

    fun sync() {
        repository.syncData()
    }

    fun selectedNotes(question: Question? = null) {
        navigateToNotesLiveData.postValue(question)
    }

}