package ro.code4.monitorizarevot.data.model.response

import com.google.gson.annotations.Expose

class PostNoteResponse {
    @Expose
    lateinit var filesAddress: List<String>
}