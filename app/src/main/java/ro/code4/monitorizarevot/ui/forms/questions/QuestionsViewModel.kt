package ro.code4.monitorizarevot.ui.forms.questions

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.koin.core.inject
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.pojo.SectionWithQuestions
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class QuestionsViewModel : BaseViewModel() {
    private val repository: Repository by inject()
    private val preferences: SharedPreferences by inject()
    private val questionsLiveData = MutableLiveData<ArrayList<ListItem>>()
    lateinit var selectedFormCode: String

    fun questions(): LiveData<ArrayList<ListItem>> = questionsLiveData

    fun getQuestions(formCode: String?) {
        formCode?.let {
            selectedFormCode = formCode
            repository.getSectionsWithQuestions(formCode).observeForever {
                processList(it)
            }
        }
    }

    private fun processList(list: List<SectionWithQuestions>?) {

    }

}