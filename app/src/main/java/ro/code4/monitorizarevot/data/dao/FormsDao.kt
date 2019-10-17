package ro.code4.monitorizarevot.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.Completable
import io.reactivex.Maybe
import ro.code4.monitorizarevot.data.model.Answer
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.data.model.Section
import ro.code4.monitorizarevot.data.model.answers.AnsweredQuestion
import ro.code4.monitorizarevot.data.model.answers.SelectedAnswer
import ro.code4.monitorizarevot.data.pojo.AnsweredQuestionPOJO
import ro.code4.monitorizarevot.data.pojo.FormWithSections
import ro.code4.monitorizarevot.data.pojo.SectionWithQuestions
import java.util.*

@Dao
interface FormsDao {
    @Query("SELECT * FROM form_details")
    fun getAllForms(): Maybe<List<FormDetails>>

    @Insert(onConflict = REPLACE)
    fun saveForm(vararg forms: FormDetails): Completable

    @Delete
    fun deleteForms(vararg forms: FormDetails): Completable

    @Query("SELECT * FROM section WHERE code=:formCode")
    fun getSectionsByCode(formCode: String): Maybe<List<Section>>

    @Query("DELETE FROM section WHERE code=:formCode")
    fun deleteSectionsByCode(formCode: String): Completable

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


    @Insert(onConflict = REPLACE)
    fun saveAnsweredQuestions(vararg answeredQuestions: AnsweredQuestion): Completable

    @Delete
    fun deleteAnsweredQuestions(vararg answeredQuestions: AnsweredQuestion): Completable

    @Query("SELECT * FROM answered_question WHERE countyCode=:countyCode AND pollingStationNumber=:branchNumber ")
    fun getAnswersFor(countyCode: String, branchNumber: Int): LiveData<List<AnsweredQuestionPOJO>>


    @Query("SELECT * FROM form_details")
    fun getFormsWithSections(): LiveData<List<FormWithSections>>

    @Query("SELECT * FROM section where formCode=:formCode")
    fun getSectionsWithQuestions(formCode: String): LiveData<List<SectionWithQuestions>>

    @Query("SELECT * FROM answered_question WHERE countyCode=:countyCode AND pollingStationNumber=:branchNumber AND formId=:formCode")
    fun getAnswersForForm(
        countyCode: String?,
        branchNumber: Int,
        formCode: String
    ): LiveData<List<AnsweredQuestionPOJO>>

    @Query("SELECT * FROM answered_question WHERE countyCode=:countyCode AND pollingStationNumber=:branchNumber AND formId=:formCode AND synced=:synced")
    fun getNotSyncedQuestionsForForm(
        countyCode: String?,
        branchNumber: Int,
        formCode: String,
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

    @Query("UPDATE answered_question SET synced=:synced WHERE countyCode=:countyCode AND pollingStationNumber=:branchNumber AND formId=:formCode")
    fun updateAnsweredQuestions(
        countyCode: String,
        branchNumber: Int,
        formCode: String,
        synced: Boolean = true
    )

    @Query("SELECT COUNT(*) FROM answered_question WHERE  synced=:synced")
    fun getCountOfNotSyncedQuestions(synced: Boolean = false): LiveData<Int>

}