package ro.code4.monitorizarevot.ui.notes

import android.annotation.SuppressLint
import android.app.Application
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.adapters.helper.NoteListItem
import ro.code4.monitorizarevot.adapters.helper.SectionListItem
import ro.code4.monitorizarevot.data.model.Note
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.helper.Constants
import ro.code4.monitorizarevot.helper.FileUtils
import ro.code4.monitorizarevot.helper.SingleLiveEvent
import ro.code4.monitorizarevot.helper.observeOnce
import ro.code4.monitorizarevot.ui.base.BaseFormViewModel
import java.io.File


class NoteViewModel : BaseFormViewModel() {

    private val app: Application by inject()
    private val notesLiveData = MutableLiveData<ArrayList<ListItem>>()
    private val filesNamesLiveData = MutableLiveData<List<String>>()
    private val submitCompletedLiveData = SingleLiveEvent<Void>()
    private var noteFiles = mutableListOf<File>()
    private val listObserver =
        Observer<List<Note>> { list ->
            processList(list)
        }


    companion object {
        @JvmStatic
        val TAG = NoteViewModel::class.java.simpleName
    }

    fun notes(): LiveData<ArrayList<ListItem>> = notesLiveData
    fun filesNames(): LiveData<List<String>> = filesNamesLiveData
    fun submitCompleted(): SingleLiveEvent<Void> = submitCompletedLiveData
    private var selectedQuestion: Question? = null
    private var fqCodes: NoteFormQuestionCodes? = null

    fun setData(question: Question?, codes: NoteFormQuestionCodes?) {
        selectedQuestion = question
        fqCodes = codes
        repository.getNotes(countyCode, municipalityCode, pollingStationNumber, selectedQuestion)
            .observeOnce(listObserver)
    }

    private fun processList(notes: List<Note>) {
        if (notes.isNotEmpty()) {
            val list = ArrayList<ListItem>(notes.size + 1)
            list.add(SectionListItem(R.string.notes_history))
            list.addAll(notes.map { NoteListItem(it) })
            notesLiveData.postValue(list)
        }
    }

    @SuppressLint("CheckResult")
    fun submit(text: String) {
        val note = Note()
        note.questionId = selectedQuestion?.id
        note.pollingStationNumber = pollingStationNumber
        note.municipalityCode = municipalityCode
        note.countyCode = countyCode
        note.description = text
        note.uriPath = concatFilePathsOrNull()
        fqCodes?.let {
            note.formCode = it.formCode
            note.questionCode = it.questionCode
        }
        repository.saveNote(note)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {},
                {
                    Log.d(TAG, it.toString())
                })
        //Writing to database is successful and we don't really need the result of the network call
        submitCompletedLiveData.call()

        selectedQuestion?.id?.let {
            repository.updateQuestionWithNotes(it)
        }
    }

    private fun concatFilePathsOrNull(): String? {
        val joinedPaths =
            noteFiles.joinToString(separator = Constants.FILES_PATHS_SEPARATOR) { it.absolutePath }
        return if (joinedPaths.isEmpty()) null else joinedPaths
    }

    /**
     * Makes copies of the file/files selected by the user so they are available as a note upload. Depending
     * on the user selection, only one of the parameters will be initialized.
     *
     * @param clipData non null if the user selects multiple files
     * @param uri non null if the user selects a single file
     */
    fun addMediaFromGallery(clipData: ClipData?, uri: Uri?) {
        if (clipData != null) {
            if (clipData.itemCount == 0) return
            // flag which will indicate if there was a problem with processing any of the files, so we can
            // show an error to the user at the end
            var hasFailedFiles = false
            for (cdItemPosition in 0 until clipData.itemCount) {
                val fileSaveStatus = runCatching {
                    FileUtils.copyFileToCache(app, clipData.getItemAt(cdItemPosition).uri)
                }
                if (fileSaveStatus.exceptionOrNull() != null) {
                    hasFailedFiles = true
                }
                fileSaveStatus.getOrNull()?.let { noteFiles.add(it) }
            }
            filesNamesLiveData.postValue(noteFiles.map { file -> file.name }.toList())
            if (hasFailedFiles) {
                messageIdToastLiveData.postValue(
                    app.getString(R.string.error_note_file_copy_multiple)
                )
            }
        } else if (uri != null) {
            runCatching { FileUtils.copyFileToCache(app, uri) }.getOrNull()?.let {
                noteFiles.add(it)
                filesNamesLiveData.postValue(noteFiles.map { file -> file.name }.toList())
            } ?: messageIdToastLiveData.postValue(
                app.getString(R.string.error_note_file_copy_single)
            )
        }
    }

    fun deleteFile(filename: String, position: Int) {
        val targetFiles = noteFiles.filter { it.absolutePath.endsWith("/$filename") }
        if (targetFiles.isNotEmpty() && targetFiles.size == 1) {
            try {
                targetFiles[0].delete()
            } catch (ex: Exception) {
                // ignored
                // this try-catch block will prevent the app from crashing if the file can't be deleted(which
                // is ok because we dereference the file below and it is safe to remain on disk)
            }
        }
        noteFiles = noteFiles.filterIndexed { index, _ -> index != position }.toMutableList()
        filesNamesLiveData.postValue(noteFiles.map { file -> file.name }.toList())
    }

    fun addUserGeneratedFile(file: File?) {
        file?.let { noteFiles.add(it) }
    }

    fun addMediaToGallery() {
        filesNamesLiveData.postValue(noteFiles.map { file -> file.name }.toList())
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        noteFiles.forEach {
            val contentUri = Uri.fromFile(it)
            mediaScanIntent.data = contentUri
            app.sendBroadcast(mediaScanIntent)
        }
    }
}
