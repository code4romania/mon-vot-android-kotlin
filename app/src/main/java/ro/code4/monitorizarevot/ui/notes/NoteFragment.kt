package ro.code4.monitorizarevot.ui.notes

import android.os.Bundle
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.android.synthetic.main.layout_edit_note.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.parceler.Parcels
import ro.code4.monitorizarevot.adapters.NoteAdapter
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.helper.Constants
import ro.code4.monitorizarevot.helper.TextWatcherDelegate
import ro.code4.monitorizarevot.ui.base.BaseFragment


class NoteFragment : BaseFragment<NoteViewModel>() {
    override val layout: Int
        get() = ro.code4.monitorizarevot.R.layout.fragment_note
    override val viewModel: NoteViewModel by viewModel()
    lateinit var adapter: NoteAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesList.layoutManager = LinearLayoutManager(activity)
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
        submitButton.setOnClickListener {
            viewModel.submit(noteInput.text)
        }

    }

    private fun setData(items: ArrayList<ListItem>) {
        if (!::adapter.isInitialized) {
            adapter = NoteAdapter(mContext, items)
        }
        notesList.adapter = adapter
    }
}