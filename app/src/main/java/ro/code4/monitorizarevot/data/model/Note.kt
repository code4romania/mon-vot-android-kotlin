package ro.code4.monitorizarevot.data.model

import com.google.gson.annotations.Expose


class Note {

    private val id: Int = 0

    @Expose
    var uriPath: String? = null

    @Expose
    var description: String? = null

    @Expose
    var questionId: Int? = null

    fun getId(): Long {
        return id.toLong()
    }
}