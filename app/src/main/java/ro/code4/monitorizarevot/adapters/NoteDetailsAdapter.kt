package ro.code4.monitorizarevot.adapters

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.ui.notes.NoteAttachment
import ro.code4.monitorizarevot.ui.notes.NoteDetails
import java.io.File

class NoteDetailsAdapter(
    context: Context
) : RecyclerView.Adapter<NoteDetailsViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)
    private var details: NoteDetails? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_NOTE_TEXT -> NoteDetailsViewHolderText(
            layoutInflater.inflate(
                R.layout.item_note_details_text,
                parent,
                false
            )
        )
        TYPE_NOTE_IMAGE -> NoteImageViewHolderDetails(
            layoutInflater.inflate(
                R.layout.item_note_details_image,
                parent,
                false
            )
        )
        else -> throw IllegalArgumentException("Unknown note row type requested!")
    }

    override fun onBindViewHolder(holder: NoteDetailsViewHolder, position: Int) {
        holder.bind(details, position)
    }

    override fun getItemViewType(position: Int) = when (position) {
        0 -> TYPE_NOTE_TEXT
        else -> TYPE_NOTE_IMAGE
    }

    override fun getItemCount() = (details?.let { 1 + (details?.attachedFiles?.size ?: 0) } ?: 0)

    fun updateAdapter(noteDetails: NoteDetails) {
        this.details = noteDetails
        notifyDataSetChanged()
    }

    companion object {
        const val TYPE_NOTE_TEXT = 1
        const val TYPE_NOTE_IMAGE = 2
    }
}

sealed class NoteDetailsViewHolder(
    rowView: View
) : RecyclerView.ViewHolder(rowView) {

    abstract fun bind(noteDetails: NoteDetails?, position: Int)
}

class NoteDetailsViewHolderText(
    private val rowView: View
) : NoteDetailsViewHolder(rowView) {
    private val formQuestionIdentifier: TextView =
        rowView.findViewById(R.id.formAndQuestionIdentifier)
    private val noteText: TextView = rowView.findViewById(R.id.noteText)
    private val noteDate: TextView = rowView.findViewById(R.id.noteDate)

    override fun bind(noteDetails: NoteDetails?, position: Int) {
        noteDetails?.let {
            formQuestionIdentifier.text = noteDetails.codes?.let { codes ->
                rowView.context.getString(
                    R.string.note_details_codes, codes.formCode, codes.questionCode
                )
            } ?: ""
            noteText.text = it.description
            noteDate.text = it.date
        }
    }
}

class NoteImageViewHolderDetails(
    private val rowView: View
) : NoteDetailsViewHolder(rowView) {

    private val noteVideoNotice: FrameLayout = rowView.findViewById(R.id.noteVideoNoticeContainer)
    private val noteImage: ImageView = rowView.findViewById(R.id.noteImage)

    override fun bind(noteDetails: NoteDetails?, position: Int) {
        noteDetails?.let {
            // the position is always offset by 1(note text information always occupies the first item in
            // the RecyclerView)
            val actualPosition = position - 1
            val attachedFile = it.attachedFiles[actualPosition]
            val isAFile = attachedFile.uri.scheme?.let { scheme -> scheme != "https" } ?: true
            if (attachedFile.isVideo) {
                noteImage.visibility = View.GONE
                noteVideoNotice.visibility = View.VISIBLE
                rowView.setOnClickListener {
                    setupExternalVideoPreview(rowView.context, attachedFile, isAFile)
                }
            } else {
                rowView.setOnClickListener(null)
                noteImage.visibility = View.VISIBLE
                noteVideoNotice.visibility = View.GONE
                val requestCreator = if (isAFile) {
                    Picasso.get().load(File(attachedFile.uri.toString()))
                } else {
                    Picasso.get().load(attachedFile.uri)
                }
                requestCreator.into(noteImage)
            }
        }
    }

    private fun setupExternalVideoPreview(
        context: Context, attachedFile: NoteAttachment, isAFile: Boolean
    ) = with(context) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            type = "video/*"
            data = if (isAFile) {
                kotlin.runCatching {
                    FileProvider.getUriForFile(
                        this@with, packageName, File(attachedFile.uri.toString())
                    )
                }.getOrNull()
            } else {
                attachedFile.uri
            }
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        if (packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(
                this, getString(R.string.note_video_previewer_missing), Toast.LENGTH_SHORT
            ).show()
        }
    }
}
