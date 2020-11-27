package ro.code4.monitorizarevot.ui.notes

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.parceler.Parcel
import org.parceler.ParcelConstructor
import ro.code4.monitorizarevot.data.model.Note
import ro.code4.monitorizarevot.helper.Constants
import ro.code4.monitorizarevot.helper.formatNoteDateTime
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class NoteDetailsViewModel : BaseViewModel() {

    private var note: Note? = null
    private val _noteDetails = MutableLiveData<NoteDetails>()
    val noteDetails: LiveData<NoteDetails> = _noteDetails

    fun setData(note: Note?) {
        note?.let {
            this.note = note
            _noteDetails.postValue(
                NoteDetails(
                    it.description,
                    note.formCode?.let { fc ->
                        note.questionCode?.let { qc -> NoteFormQuestionCodes(fc, qc) }
                    },
                    it.date.formatNoteDateTime(),
                    unwrapNoteUrls(it),
                    it.synced
                )
            )
        }
    }

    private fun unwrapNoteUrls(note: Note): List<NoteAttachment> {
        val paths = note.uriPath?.split(Constants.FILES_PATHS_SEPARATOR) ?: emptyList()
        return paths.filter { it.isNotEmpty() }.map { NoteAttachment(Uri.parse(it), isVideo(it)) }
    }

    private fun isVideo(path: String): Boolean {
        val videosExtensions = listOf("mp4", "m4a", "3gp", "ts", "flac", "amr", "ogg", "wav", "mkv")
        val lastPointIndex = path.lastIndexOf(".")
        return if (lastPointIndex > 0 && lastPointIndex < path.length - 1) {
            val extension = path.substring((lastPointIndex + 1) until path.length)
            videosExtensions.contains(extension)
        } else {
            false
        }
    }
}

data class NoteDetails(
    val description: String,
    val codes: NoteFormQuestionCodes?,
    val date: String,
    val attachedFiles: List<NoteAttachment>,
    val isSynced: Boolean
)

data class NoteAttachment(
    val uri: Uri,
    val isVideo: Boolean
)

@Parcel(Parcel.Serialization.BEAN)
data class NoteFormQuestionCodes @ParcelConstructor constructor(
    val formCode: String,
    val questionCode: String
)