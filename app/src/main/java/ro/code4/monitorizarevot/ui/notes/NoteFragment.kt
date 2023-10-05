package ro.code4.monitorizarevot.ui.notes
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.android.synthetic.main.layout_edit_note.*
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.parceler.Parcels
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.NoteDelegationAdapter
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.helper.*
import ro.code4.monitorizarevot.helper.Constants.REQUEST_CODE_RECORD_VIDEO
import ro.code4.monitorizarevot.helper.Constants.REQUEST_CODE_TAKE_PHOTO
import ro.code4.monitorizarevot.ui.base.ViewModelFragment
import ro.code4.monitorizarevot.ui.forms.FormsViewModel

class NoteFragment : ViewModelFragment<NoteViewModel>(), PermissionRequest.Listener {

    override val layout: Int
        get() = R.layout.fragment_note
    override val screenName: Int
        get() = R.string.analytics_title_notes

    companion object {
        val TAG = NoteFragment::class.java.simpleName
    }

    override val viewModel: NoteViewModel by viewModel()
    private lateinit var baseViewModel: FormsViewModel
    private var fqCodes: NoteFormQuestionCodes? = null
    private val noteAdapter: NoteDelegationAdapter by lazy {
        NoteDelegationAdapter { note -> baseViewModel.selectNote(note) }
    }

    private val request by lazy {
        permissionsBuilder(
            Manifest.permission.CAMERA
        ).build()
    }

    private val pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.addMediaFromGallery(*uris.toTypedArray())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseViewModel = getSharedViewModel(from = { requireParentFragment() })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        request.addListener(this)
        request.addListener {
            if (it.anyPermanentlyDenied()) {
                showPermissionRationale(true)
            }
        }

        val noteFileContainer = view.findViewById<LinearLayout>(R.id.noteFilesContainer)
        notesList.layoutManager = LinearLayoutManager(mContext)
        notesList.adapter = noteAdapter
        notesList.addItemDecoration(
            HorizontalDividerItemDecoration.Builder(mContext)
                .color(Color.TRANSPARENT)
                .sizeResId(R.dimen.small_margin).build()
        )
        viewModel.title().observe(viewLifecycleOwner, Observer {
            baseViewModel.setTitle(it)
        })

        val selectedForm: FormDetails? = baseViewModel.selectedForm().value
        val selectedQuestion: Question? = arguments?.let {
            Parcels.unwrap<Question>(it.getParcelable(Constants.QUESTION))
        }
        fqCodes = if (selectedForm != null && selectedQuestion != null) {
            NoteFormQuestionCodes(selectedForm.code, selectedQuestion.code)
        } else {
            null
        }
        viewModel.setData(selectedQuestion, fqCodes)

        viewModel.notes().observe(viewLifecycleOwner, Observer {
            noteAdapter.items = it
        })
        viewModel.filesNames().observe(viewLifecycleOwner, Observer {
            noteFileContainer.visibility = View.VISIBLE
            noteFileContainer.removeAllViews()
            it.forEachIndexed { index, filename ->
                val attachmentView = requireActivity().layoutInflater.inflate(
                    R.layout.include_note_filename, noteFileContainer, false
                ).also { view ->
                    view.findViewById<TextView>(R.id.filenameText).text = filename
                    view.findViewById<ImageButton>(R.id.deleteFile).setOnClickListener {
                        viewModel.deleteFile(filename, index)
                    }
                }
                noteFileContainer.addView(attachmentView)
            }
        })
        viewModel.submitCompleted().observe(viewLifecycleOwner, Observer {
            activity?.onBackPressed()
        })

        viewModel.setTitle(getString(R.string.title_note))
        noteInput.setOnTouchListener { view, motionEvent ->
            if (view == noteInput) {
                view.parent.requestDisallowInterceptTouchEvent(true)
                when (motionEvent.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }

        noteInput.addTextChangedListener(object : TextWatcher by TextWatcherDelegate {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                submitButton.isEnabled = !p0.isNullOrEmpty()
            }
        })
        addMediaButton.setOnClickListener {
                 request.send()
        }

        submitButton.setOnClickListener {
            viewModel.submit(noteInput.text.toString())
        }
    }

    @SuppressLint("RestrictedApi")
    private fun showPopupMenu() {
        val popup = PopupMenu(mContext, addMediaButton)
        popup.menuInflater.inflate(R.menu.menu_note_media, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.note_gallery -> pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                R.id.note_photo -> {
                    val file = takePicture()
                    viewModel.addUserGeneratedFile(file)
                }
                R.id.note_video -> {
                    val file = takeVideo()
                    viewModel.addUserGeneratedFile(file)
                }
            }
            true
        }
        val menuHelper = MenuPopupHelper(mContext, popup.menu as MenuBuilder, addMediaButton)
        menuHelper.setForceShowIcon(true)
        menuHelper.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_RECORD_VIDEO, REQUEST_CODE_TAKE_PHOTO -> {
                    viewModel.addMediaToGallery()
                }
            }
        }
    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        val context = requireContext()
        when {
            result.anyPermanentlyDenied() -> showPermissionRationale(true)
            result.anyShouldShowRationale() -> showPermissionRationale()
            result.allGranted() -> showPopupMenu()
        }
    }

    private fun showPermissionRationale(isPermanentlyDenied: Boolean = false) {
        val (titleResId, message, positiveButton, positiveAction) = if (isPermanentlyDenied) {
            arrayOf(
                R.string.permission_permanently_denied_title,
                R.string.permission_permanently_denied_msg,
                R.string.permission_permanently_denied_settings_button,
                object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        openAppSettings()
                    }

                })
        } else {
            arrayOf(
                R.string.permission_denied_title,
                R.string.permission_denied_msg,
                R.string.permission_denied_ok_button,
                object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        request.send()
                    }
                })

        }
        AlertDialog.Builder(mContext, R.style.AlertDialog)
            .setTitle(titleResId as Int)
            .setMessage(message as Int)
            .setNegativeButton(
                R.string.permission_denied_cancel_button
            ) { p0, _ -> p0.dismiss() }
            .setPositiveButton(
                positiveButton as Int,
                positiveAction as DialogInterface.OnClickListener
            )
            .show()

    }

    private fun openAppSettings() {
        activity?.apply {
            startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY)
            )
        }
    }
}