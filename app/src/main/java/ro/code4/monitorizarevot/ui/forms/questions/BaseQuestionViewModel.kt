package ro.code4.monitorizarevot.ui.forms.questions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.model.Note
import ro.code4.monitorizarevot.data.pojo.AnsweredQuestionPOJO
import ro.code4.monitorizarevot.data.pojo.SectionWithQuestions
import ro.code4.monitorizarevot.ui.base.BaseFormViewModel

abstract class BaseQuestionViewModel : BaseFormViewModel() {
    val questionsLiveData = MutableLiveData<ArrayList<ListItem>>()
    var selectedFormId: Int = -1
    fun questions(): LiveData<ArrayList<ListItem>> = questionsLiveData

    private fun getQuestions(formId: Int) {
        selectedFormId = formId
        val noteSource = provideNoteSource()
        val baseSource = if (noteSource != null) {
            Observable.combineLatest(
                repository.getSectionsWithQuestions(formId),
                repository.getAnswersForForm(countyCode, communityCode, pollingStationNumber, formId),
                noteSource,
                Function3 { t1, t2, t3 ->
                    t1.forEach {
                        it.questions.forEach { q ->
                            q.question.hasNotes = t3.any { n -> n.questionId == q.question.id }
                        }
                    }
                    Pair(t1, t2)
                }
            )
        } else {
            Observable.combineLatest(repository.getSectionsWithQuestions(formId),
                repository.getAnswersForForm(countyCode, communityCode, pollingStationNumber, formId),
                BiFunction<List<SectionWithQuestions>, List<AnsweredQuestionPOJO>, Pair<List<SectionWithQuestions>, List<AnsweredQuestionPOJO>>> { t1, t2 ->
                    Pair(t1, t2)
                })
        }
        disposables.add(baseSource.subscribeOn(Schedulers.computation())
            .map { dataPair ->
                // sort on orderNumber the sections along with their questions and answers
                val sortedSections = dataPair.first.sortedBy { it.section.orderNumber }
                for (sortedSection in sortedSections) {
                    val sortedQuestions =
                        sortedSection.questions.sortedBy { it.question.orderNumber }
                    for (sortedQuestion in sortedQuestions) {
                        sortedQuestion.answers = sortedQuestion.answers?.sortedBy { it.orderNumber }
                    }
                    sortedSection.questions = sortedQuestions
                }
                Pair(sortedSections, dataPair.second)
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribe { result -> processList(result.first, result.second) })
    }

    // TODO this method should also run on a background thread to avoid doing lengthy
    //  operations(like sorting or iterating over large collections) on the main thread
    abstract fun processList(
        sections: List<SectionWithQuestions>,
        answersForForm: List<AnsweredQuestionPOJO>
    )

    open fun provideNoteSource(): Observable<List<Note>>? = null

    fun setData(formDetails: FormDetails) {
        getQuestions(formDetails.id)
        setTitle(formDetails.description)
    }

}