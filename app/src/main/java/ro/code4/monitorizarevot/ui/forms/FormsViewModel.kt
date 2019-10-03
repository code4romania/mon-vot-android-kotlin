package ro.code4.monitorizarevot.ui.forms

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.monitorizarevot.adapters.FormAdapter.Companion.TYPE_FORM
import ro.code4.monitorizarevot.adapters.FormAdapter.Companion.TYPE_NOTE
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.data.pojo.AnsweredQuestionPOJO
import ro.code4.monitorizarevot.data.pojo.FormWithSections
import ro.code4.monitorizarevot.helper.getBranchNumber
import ro.code4.monitorizarevot.helper.getCountyCode
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class FormsViewModel : BaseViewModel() {
    private val repository: Repository by inject()
    private val preferences: SharedPreferences by inject()
    private val formsLiveData = MutableLiveData<ArrayList<ListItem>>()
    private val answersLiveData = MutableLiveData<List<AnsweredQuestionPOJO>>()
    private val formsWithSections = MutableLiveData<List<FormWithSections>>()
    private val selectedFormLiveData = MutableLiveData<FormDetails>()
    private val selectedQuestionLiveData = MutableLiveData<Pair<FormDetails, Question>>()
    private val syncVisibilityLiveData = MutableLiveData<Int>()
    private val navigateToNotesLiveData = MutableLiveData<Question?>()

    init {

        getForms()
    }

    private fun subscribe() {
        repository.getAnswers(preferences.getCountyCode()!!, preferences.getBranchNumber())
            .observeForever {
                answersLiveData.value = it
                processList()
                syncVisibilityLiveData.postValue(if (it.any { item -> !item.answeredQuestion.synced }) View.VISIBLE else View.GONE)
            }
        repository.getFormsWithQuestions().observeForever {
            formsWithSections.value = it
            processList()
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
            .subscribe({


            }, {
                onError(it)
            })

    }

    private fun processList() {
        formsWithSections.value?.let { list ->
            val items = ArrayList<ListItem>()
            val answeredQuestionsPOJOs = answersLiveData.value
            answeredQuestionsPOJOs?.forEach { pojo ->
                list.find { pojo.answeredQuestion.formCode == it.form.code }
                    ?.incrementNoOfAnsweredQuestions()
            }
            items.addAll(list.map { ListItem(TYPE_FORM, it) })
            items.add(ListItem(TYPE_NOTE))
            formsLiveData.postValue(items)
        }
    }

    fun selectForm(formDetails: FormDetails) {
        selectedFormLiveData.postValue(formDetails)
    }

    fun selectQuestion(question: Question) {
        selectedQuestionLiveData.postValue(Pair(selectedFormLiveData.value!!, question))
    }

    fun syncVisibility(): LiveData<Int> = syncVisibilityLiveData

    fun sync() {
        repository.syncAnswers()
    }

    fun selectedNotes(question: Question? = null) {
        navigateToNotesLiveData.postValue(question)
    }

}