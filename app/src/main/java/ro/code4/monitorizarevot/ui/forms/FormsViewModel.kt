package ro.code4.monitorizarevot.ui.forms

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ro.code4.monitorizarevot.adapters.helper.AddNoteListItem
import ro.code4.monitorizarevot.adapters.helper.FormListItem
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.data.pojo.AnsweredQuestionPOJO
import ro.code4.monitorizarevot.data.pojo.BranchDetailsInfo
import ro.code4.monitorizarevot.data.pojo.FormWithSections
import ro.code4.monitorizarevot.helper.zipLiveData
import ro.code4.monitorizarevot.ui.base.BaseFormViewModel

class FormsViewModel : BaseFormViewModel() {
    private val formsLiveData = MutableLiveData<ArrayList<ListItem>>()
    private val selectedFormLiveData = MutableLiveData<FormDetails>()
    private val selectedQuestionLiveData = MutableLiveData<Pair<FormDetails, Question>>()
    private val syncVisibilityLiveData = MutableLiveData<Int>()
    private val navigateToNotesLiveData = MutableLiveData<Question?>()
    private val branchDetailsLiveData = MutableLiveData<BranchDetailsInfo>()

    init {
        getForms()
        getBranchBarText()
    }

    private fun subscribe() {
        zipLiveData(
            repository.getNotSyncedQuestions(),
            repository.getNotSyncedNotes(),
            repository.getNotSyncedBranchDetails()
        ).observeForever {
            syncVisibilityLiveData.postValue(
                if (it.fold(
                        0,
                        { acc, obj -> acc + obj }) != 0
                ) View.VISIBLE else View.GONE
            )
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
    fun branchDetails(): LiveData<BranchDetailsInfo> = branchDetailsLiveData

    private fun getBranchBarText() {
        disposables.add(
            repository.getBranchInfo(
                countyCode,
                branchNumber
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    branchDetailsLiveData.postValue(it)
                }, {
                    onError(it)
                })
        )

    }

    @SuppressLint("CheckResult")
    fun getForms() {
        disposables.add(
            repository.getForms()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {
                    onError(it)
                })
        )

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