package ro.code4.monitorizarevot.ui.notes

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.NoteAdapter.Companion.TYPE_NOTE
import ro.code4.monitorizarevot.adapters.NoteAdapter.Companion.TYPE_SECTION
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.model.Note
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.helper.getBranchNumber
import ro.code4.monitorizarevot.helper.getCountyCode
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class NoteViewModel : BaseViewModel() {
    private val repository: Repository by inject()
    private val preferences: SharedPreferences by inject()
    private var countyCode: String
    private var branchNumber: Int = -1
    private val notesLiveData = MutableLiveData<ArrayList<ListItem>>()

    init {
        countyCode = preferences.getCountyCode()!!
        branchNumber = preferences.getBranchNumber()
    }

    fun notes(): LiveData<ArrayList<ListItem>> = notesLiveData
    private var selectedQuestion: Question? = null
    fun setData(question: Question?) {
        selectedQuestion = question
        repository.getNotes(countyCode, branchNumber, selectedQuestion).observeForever {
            processList(it)
        }
    }

    private fun processList(notes: List<Note>) {
        if (notes.isNotEmpty()) {
            val list = ArrayList<ListItem>(notes.size + 1)
            list.add(ListItem(TYPE_SECTION, R.string.notes_history))
            list.addAll(notes.map { ListItem(TYPE_NOTE, it) })
            notesLiveData.postValue(list)
        }
    }

    fun submit(text: String) {
        val note = Note()
        note.questionId = selectedQuestion?.id
        note.branchNumber = branchNumber
        note.countyCode = countyCode
        note.description = text
        repository.saveNote(note).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

}