package ro.code4.monitorizarevot.ui.notes

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.android.synthetic.main.layout_edit_note.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.parceler.Parcels
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.NoteAdapter
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.helper.*
import ro.code4.monitorizarevot.helper.Constants.REQUEST_CODE_GALLERY
import ro.code4.monitorizarevot.helper.Constants.REQUEST_CODE_RECORD_VIDEO
import ro.code4.monitorizarevot.helper.Constants.REQUEST_CODE_TAKE_PHOTO
import ro.code4.monitorizarevot.ui.base.BaseFragment

class NoteFragment : BaseFragment<NoteViewModel>(), PermissionManager.PermissionListener {


    override val layout: Int
        get() = R.layout.fragment_note
    override val viewModel: NoteViewModel by viewModel()
    lateinit var adapter: NoteAdapter
    private lateinit var permissionManager: PermissionManager
    override fun onAttach(context: Context) {
        super.onAttach(context)
        permissionManager = PermissionManager(activity!!, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesList.layoutManager = LinearLayoutManager(mContext)
        notesList.addItemDecoration(
            HorizontalDividerItemDecoration.Builder(mContext)
                .color(Color.TRANSPARENT)
                .sizeResId(R.dimen.small_margin).build()
        )
        viewModel.setData(Parcels.unwrap<Question>(arguments?.getParcelable((Constants.QUESTION))))
        viewModel.notes().observe(this, Observer {
            setData(it)
        })
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
    }

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
                R.id.note_photo -> takePicture()
                R.id.note_video -> takeVideo()
            }
            true
        }
        val menuHelper = MenuPopupHelper(mContext, popup.menu as MenuBuilder, addMediaButton)
        menuHelper.setForceShowIcon(true)
        menuHelper.show()
    }

    private fun setData(items: ArrayList<ListItem>) {
        if (!::adapter.isInitialized) {
            adapter = NoteAdapter(mContext, items)
        }
        notesList.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_RECORD_VIDEO -> {

                }
                REQUEST_CODE_TAKE_PHOTO -> {

                }
                REQUEST_CODE_GALLERY -> {

                }
            }
        }
    }

    @Suppress("SameParameterValue")
    override fun onPermissionsGranted() {
        showPopupMenu()
    }

    override fun onPermissionDenied(
        vararg allPermissions: String,
        permissionsDenied: List<String>
    ) {
        //todo show explanation
    }
}