package ro.code4.monitorizarevot.ui.notes

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.android.synthetic.main.layout_edit_note.*
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.parceler.Parcels
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.NoteDelegationAdapter
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.helper.*
import ro.code4.monitorizarevot.helper.Constants.REQUEST_CODE_GALLERY
import ro.code4.monitorizarevot.helper.Constants.REQUEST_CODE_RECORD_VIDEO
import ro.code4.monitorizarevot.helper.Constants.REQUEST_CODE_TAKE_PHOTO
import ro.code4.monitorizarevot.ui.base.BaseAnalyticsFragment
import ro.code4.monitorizarevot.ui.base.ViewModelFragment
import ro.code4.monitorizarevot.ui.forms.FormsViewModel

class NoteFragment : ViewModelFragment<NoteViewModel>(), PermissionManager.PermissionListener {

    override val layout: Int
        get() = R.layout.fragment_note
    override val screenName: Int
        get() = R.string.analytics_title_notes

    companion object {
        val TAG = NoteFragment::class.java.simpleName
    }
    override val viewModel: NoteViewModel by viewModel()
    private lateinit var baseViewModel: FormsViewModel
    private val noteAdapter: NoteDelegationAdapter by lazy { NoteDelegationAdapter() }
    private lateinit var permissionManager: PermissionManager
    override fun onAttach(context: Context) {
        super.onAttach(context)
        permissionManager = PermissionManager(activity!!, this)
        baseViewModel = getSharedViewModel(from = { parentFragment!! })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        viewModel.setData(Parcels.unwrap<Question>(arguments?.getParcelable((Constants.QUESTION))))
        viewModel.notes().observe(viewLifecycleOwner, Observer {
            noteAdapter.items = it
        })
        viewModel.fileName().observe(viewLifecycleOwner, Observer {
            filenameText.text = it
            filenameText.visibility = View.VISIBLE
            addMediaButton.visibility = View.GONE
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
            checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        submitButton.setOnClickListener {
            viewModel.submit(noteInput.text.toString())

        }
        setupHideKeyboardListeners(note_scroll_view)
    }

    @Suppress("SameParameterValue")
    private fun checkPermissions(permission: String) {
        permissionManager.checkPermissions(permission, listener = this)
    }

    @SuppressLint("RestrictedApi")
    private fun showPopupMenu() {
        val popup = PopupMenu(mContext, addMediaButton)
        popup.menuInflater.inflate(R.menu.menu_note_media, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.note_gallery -> openGallery()
                R.id.note_photo -> {
                    val file = takePicture()
                    viewModel.addFile(file)
                }
                R.id.note_video -> {
                    val file = takeVideo()
                    viewModel.addFile(file)
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
                REQUEST_CODE_GALLERY -> {
                    viewModel.getMediaFromGallery(data?.data)
                }
            }
        }
    }

    @Suppress("SameParameterValue")
    override fun onPermissionsGranted() {
        showPopupMenu()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PermissionManager.PERMISSION_REQUEST && grantResults.isNotEmpty()) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED } && grantResults.size == permissions.size) {
                onPermissionsGranted()
            } else {
                checkDeniedPermissions(permissions, grantResults)
            }
        }

    }

    private fun checkDeniedPermissions(permissions: Array<out String>, grantResults: IntArray) {
        val permanentlyDenied = permissions.filterIndexed { index, s ->
            grantResults[index] == PackageManager.PERMISSION_DENIED
                    && !permissionManager.checkShouldShowRequestPermissionsRationale(s)
        }

        if (permanentlyDenied.isNotEmpty()) {
            showPermissionRationale(true)
        } else {
            val denied = permissions.filterIndexed { index, s ->
                grantResults[index] == PackageManager.PERMISSION_DENIED
                        && permissionManager.checkShouldShowRequestPermissionsRationale(s)
            }
            if (denied.isNotEmpty()) {
                showPermissionRationale()
            }
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
                        permissionManager.requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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

    override fun onPermissionDenied(
        vararg allPermissions: String,
        permissionsDenied: List<String>
    ) {
        showPermissionRationale()
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
    private fun setupHideKeyboardListeners(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener(View.OnTouchListener { v, event ->
                hideKeyboard()
                false
            })
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupHideKeyboardListeners(innerView)
            }
        }
    }
}