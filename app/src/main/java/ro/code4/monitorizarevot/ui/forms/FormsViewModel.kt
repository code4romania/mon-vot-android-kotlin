package ro.code4.monitorizarevot.ui.forms

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ro.code4.monitorizarevot.adapters.helper.AddNoteListItem
import ro.code4.monitorizarevot.adapters.helper.FormListItem
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.data.pojo.AnsweredQuestionPOJO
import ro.code4.monitorizarevot.data.pojo.FormWithSections
import ro.code4.monitorizarevot.data.pojo.PollingStationInfo
import ro.code4.monitorizarevot.helper.Constants.REMOTE_CONFIG_FILTER_DIASPORA_FORMS
import ro.code4.monitorizarevot.helper.completedPollingStationConfig
import ro.code4.monitorizarevot.helper.zipLiveData
import ro.code4.monitorizarevot.ui.base.BaseFormViewModel

class FormsViewModel : BaseFormViewModel() {
    private val formsLiveData = MutableLiveData<ArrayList<ListItem>>()
    private val selectedFormLiveData = MutableLiveData<FormDetails>()
    private val selectedQuestionLiveData = MutableLiveData<Pair<FormDetails, Question>>()
    private val syncVisibilityLiveData = MediatorLiveData<Int>()
    private val navigateToNotesLiveData = MutableLiveData<Question?>()
    private val pollingStationLiveData = MutableLiveData<PollingStationInfo>()

    init {
        getForms()
        getPollingStationBarText()
    }

    private fun subscribe() {

        val notSyncedQuestionsCount = repository.getNotSyncedQuestions()
        val notSyncedNotesCount = repository.getNotSyncedNotes()
        val notSyncedPollingStationsCount = repository.getNotSyncedPollingStationsCount()
        fun update() {
            syncVisibilityLiveData.value =
                if ((notSyncedQuestionsCount.value ?: 0) + (notSyncedNotesCount.value
                        ?: 0) + (notSyncedPollingStationsCount.value ?: 0) > 0
                ) View.VISIBLE else View.GONE
        }
        syncVisibilityLiveData.addSource(notSyncedQuestionsCount) {
            update()
        }
        syncVisibilityLiveData.addSource(notSyncedNotesCount) {
            update()
        }
        syncVisibilityLiveData.addSource(notSyncedPollingStationsCount) {
            update()
        }

        zipLiveData(
            repository.getAnswers(countyCode, pollingStationNumber),
            repository.getFormsWithQuestions()
        ).observeForever {
            processList(it.first, it.second)
        }
    }

    fun forms(): LiveData<ArrayList<ListItem>> = formsLiveData

    fun selectedForm(): LiveData<FormDetails> = selectedFormLiveData
    fun selectedQuestion(): LiveData<Pair<FormDetails, Question>> = selectedQuestionLiveData
    fun navigateToNotes(): LiveData<Question?> = navigateToNotesLiveData
    fun pollingStation(): LiveData<PollingStationInfo> = pollingStationLiveData

    private fun getPollingStationBarText() {
        disposables.add(
            repository.getPollingStationInfo(
                countyCode,
                pollingStationNumber
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    subscribe()
                }
                .subscribe({
                    pollingStationLiveData.postValue(it)
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
                answers.count { it.answeredQuestion.formId == formWithSections.form.id }
        }
        val filterDiasporaForms = try {
            FirebaseRemoteConfig.getInstance().getBoolean(REMOTE_CONFIG_FILTER_DIASPORA_FORMS)
        } catch (e: Exception) {
            false
        }
        var formsList = when {
            !filterDiasporaForms || pollingStationLiveData.value?.isDiaspora == true -> forms.map {
                FormListItem(it)
            }
            else -> forms.filter { it.form.diaspora == false }.map { FormListItem(it) }
        }
        formsList = formsList.sortedBy { it.formWithSections.form.order }
        items.addAll(formsList)
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

    fun notifyChangeRequested() {
        preferences.completedPollingStationConfig(false)
    }

}