package ro.code4.monitorizarevot.ui.forms.questions

import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.adapters.helper.MultiChoiceListItem
import ro.code4.monitorizarevot.adapters.helper.QuestionDetailsListItem
import ro.code4.monitorizarevot.adapters.helper.SingleChoiceListItem
import ro.code4.monitorizarevot.data.model.answers.AnsweredQuestion
import ro.code4.monitorizarevot.data.model.answers.SelectedAnswer
import ro.code4.monitorizarevot.data.pojo.AnsweredQuestionPOJO
import ro.code4.monitorizarevot.data.pojo.SectionWithQuestions
import ro.code4.monitorizarevot.helper.Constants.TYPE_MULTI_CHOICE
import ro.code4.monitorizarevot.helper.Constants.TYPE_MULTI_CHOICE_DETAILS
import ro.code4.monitorizarevot.helper.Constants.TYPE_SINGLE_CHOICE
import ro.code4.monitorizarevot.helper.Constants.TYPE_SINGLE_CHOICE_DETAILS

class QuestionsDetailsViewModel : BaseQuestionViewModel() {

    override fun processList(
        sections: List<SectionWithQuestions>,
        answersForForm: List<AnsweredQuestionPOJO>
    ) {
        val list = ArrayList<ListItem>()
        sections.forEach { sectionWithQuestion ->
            sectionWithQuestion.questions.forEach { questionWithAnswers ->
                questionWithAnswers.answers?.forEach { answer ->
                    val answeredQuestion =
                        answersForForm.find { it.answeredQuestion.questionId == questionWithAnswers.question.id }
                    answeredQuestion?.also { savedQuestion ->
                        val selectedAnswer =
                            savedQuestion.selectedAnswers.find { it.optionId == answer.idOption }
                        questionWithAnswers.question.savedLocally =
                            savedQuestion.answeredQuestion.savedLocally
                        questionWithAnswers.question.synced = savedQuestion.answeredQuestion.synced
                        if (selectedAnswer != null) {
                            answer.selected = true
                            if (answer.isFreeText) {
                                answer.value = selectedAnswer.value.orEmpty()
                            }
                        }
                    }
                }
                when (questionWithAnswers.question.questionType) {
                    TYPE_SINGLE_CHOICE, TYPE_SINGLE_CHOICE_DETAILS -> list.add(
                        SingleChoiceListItem(questionWithAnswers)
                    )
                    TYPE_MULTI_CHOICE, TYPE_MULTI_CHOICE_DETAILS -> list.add(
                        MultiChoiceListItem(questionWithAnswers)
                    )
                }
            }
        }
        questionsLiveData.postValue(list)
    }

    fun saveAnswer(listItem: QuestionDetailsListItem) {
        with(listItem.questionWithAnswers) {
            if (question.synced) {
                return
            }
            answers?.filter { it.selected }?.also {
                if (it.isNotEmpty()) {
                    val answeredQuestion = AnsweredQuestion(
                        question.id,
                        countyCode,
                        pollingStationNumber,
                        selectedFormId
                    )
                    val list = it.map { answer ->
                        SelectedAnswer(
                            answer.idOption,
                            countyCode,
                            pollingStationNumber,
                            answeredQuestion.id,
                            if (answer.isFreeText) answer.value else null
                        )
                    }
                    repository.saveAnsweredQuestion(answeredQuestion, list)
                }
            }
        }
    }

    fun syncData() {
        repository.syncAnswers(countyCode, pollingStationNumber, selectedFormId)
    }

}