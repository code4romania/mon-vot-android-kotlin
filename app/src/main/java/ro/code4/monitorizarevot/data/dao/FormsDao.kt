package ro.code4.monitorizarevot.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import ro.code4.monitorizarevot.data.model.Answer
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.data.model.Section
import ro.code4.monitorizarevot.data.model.answers.AnsweredQuestion
import ro.code4.monitorizarevot.data.model.answers.SelectedAnswer
import ro.code4.monitorizarevot.data.pojo.AnsweredQuestionPOJO
import ro.code4.monitorizarevot.data.pojo.FormWithSections
import ro.code4.monitorizarevot.data.pojo.SectionWithQuestions


@Dao
interface FormsDao {
    @Query("SELECT * FROM form_details")
    fun getAllForms(): Maybe<List<FormDetails>>

    @Insert(onConflict = REPLACE)
    fun saveForm(vararg forms: FormDetails): Completable

    @Delete
    fun deleteForms(vararg forms: FormDetails): Completable

    @Query("SELECT * FROM section WHERE code=:formId")
    fun getSectionsByCode(formId: Int): Maybe<List<Section>>

    @Query("DELETE FROM section WHERE code=:formId")
    fun deleteSectionsByCode(formId: Int): Completable

    @Transaction
    fun save(vararg sections: Section) {
        saveSections(*sections)
        val questions = sections.fold(ArrayList<Question>(), { list, section ->
            list.addAll(section.questions)
            list
        })
        saveQuestions(*questions.map { it }.toTypedArray())
        val answers = questions.fold(ArrayList<Answer>(), { list, question ->
            list.addAll(question.optionsToQuestions)
            list
        })
        saveAnswers(*answers.map { it }.toTypedArray())
    }

    @Insert(onConflict = REPLACE)
    fun saveSections(vararg sections: Section)

    @Insert(onConflict = REPLACE)
    fun saveQuestions(vararg questions: Question)

    @Insert(onConflict = REPLACE)
    fun saveAnswers(vararg answers: Answer)

    @Query("DELETE FROM answered_question WHERE id=:id")
    fun deleteAnsweredQuestion(id: String): Completable

    @Query("SELECT * FROM answered_question WHERE countyCode=:countyCode AND pollingStationNumber=:pollingStationNumber ")
    fun getAnswersFor(
        countyCode: String,
        pollingStationNumber: Int
    ): Observable<List<AnsweredQuestionPOJO>>


    @Query("SELECT * FROM form_details ORDER BY `order`")
    fun getFormsWithSections(): Observable<List<FormWithSections>>

    @Query("SELECT * FROM section where formId=:formId ORDER BY orderNumber")
    fun getSectionsWithQuestions(formId: Int): Observable<List<SectionWithQuestions>>

    @Query("SELECT * FROM answered_question WHERE countyCode=:countyCode AND pollingStationNumber=:pollingStationNumber AND formId=:formId")
    fun getAnswersForForm(
        countyCode: String?,
        pollingStationNumber: Int,
        formId: Int
    ): Observable<List<AnsweredQuestionPOJO>>

    @Query("SELECT * FROM answered_question WHERE countyCode=:countyCode AND pollingStationNumber=:pollingStationNumber AND formId=:formId AND synced=:synced")
    fun getNotSyncedQuestionsForForm(
        countyCode: String?,
        pollingStationNumber: Int,
        formId: Int,
        synced: Boolean = false
    ): Maybe<List<AnsweredQuestionPOJO>>

    @Query("SELECT * FROM answered_question WHERE  synced=:synced")
    fun getNotSyncedQuestions(synced: Boolean = false): Maybe<List<AnsweredQuestionPOJO>>

    @Transaction
    fun insertAnsweredQuestion(answeredQuestion: AnsweredQuestion, answers: List<SelectedAnswer>) {
        insertAnsweredQuestion(answeredQuestion)
        insertAnswers(*answers.map { it }.toTypedArray())
        answeredQuestion.savedLocally = true
        updateAnsweredQuestion(answeredQuestion)
    }

    @Insert(onConflict = REPLACE)
    fun insertAnsweredQuestion(answeredQuestion: AnsweredQuestion)

    @Update
    fun updateAnsweredQuestion(vararg answeredQuestion: AnsweredQuestion)

    @Insert(onConflict = REPLACE)
    fun insertAnswers(vararg answers: SelectedAnswer)

    @Query("UPDATE answered_question SET synced=:synced WHERE countyCode=:countyCode AND pollingStationNumber=:pollingStationNumber AND formId=:formId")
    fun updateAnsweredQuestions(
        countyCode: String,
        pollingStationNumber: Int,
        formId: Int,
        synced: Boolean = true
    )

    @Query("UPDATE question SET hasNotes=:hasNotes WHERE id=:questionId")
    fun updateQuestionWithNotes(
        questionId: Int,
        hasNotes: Boolean = true
    )

    @Query("SELECT COUNT(*) FROM answered_question WHERE  synced=:synced")
    fun getCountOfNotSyncedQuestions(synced: Boolean = false): LiveData<Int>

}