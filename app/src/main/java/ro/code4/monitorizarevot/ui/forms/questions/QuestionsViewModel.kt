package ro.code4.monitorizarevot.ui.forms.questions

import io.reactivex.Observable
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.adapters.helper.QuestionListItem
import ro.code4.monitorizarevot.adapters.helper.SectionListItem
import ro.code4.monitorizarevot.data.model.Note
import ro.code4.monitorizarevot.data.pojo.AnsweredQuestionPOJO
import ro.code4.monitorizarevot.data.pojo.SectionWithQuestions

class QuestionsViewModel : BaseQuestionViewModel() {
    override fun processList(
        sections: List<SectionWithQuestions>,
        answersForForm: List<AnsweredQuestionPOJO>
    ) {
        val list = ArrayList<ListItem>()
        sections.forEachIndexed { index, sectionWithQuestion ->
            list.add(
                SectionListItem(
                    R.string.section_title,
                    index + 1,
                    sectionWithQuestion.section.description.orEmpty()
                )
            )
            list.addAll(sectionWithQuestion.questions.map { questionWithAnswers ->
                val answeredQuestion =
                    answersForForm.find { it.answeredQuestion.questionId == questionWithAnswers.question.id }
                answeredQuestion?.let {
                    questionWithAnswers.question.synced = it.answeredQuestion.synced
                    questionWithAnswers.question.savedLocally = it.answeredQuestion.savedLocally
                }
                QuestionListItem(questionWithAnswers.question)
            })
        }
        questionsLiveData.postValue(list)
    }

    override fun provideNoteSource(): Observable<List<Note>> {
        return repository.getNotesAsObservable(countyCode, pollingStationNumber, null)
            .onErrorReturnItem(emptyList())
    }
}