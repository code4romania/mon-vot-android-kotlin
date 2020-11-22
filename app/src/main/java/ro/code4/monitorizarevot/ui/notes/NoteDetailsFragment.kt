package ro.code4.monitorizarevot.ui.notes

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_note_detail.*
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import org.koin.android.viewmodel.ext.android.getViewModel
import org.parceler.Parcels
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.NoteDetailsAdapter
import ro.code4.monitorizarevot.data.model.Note
import ro.code4.monitorizarevot.helper.Constants
import ro.code4.monitorizarevot.helper.isOnline
import ro.code4.monitorizarevot.ui.base.ViewModelFragment
import ro.code4.monitorizarevot.ui.forms.FormsViewModel

class NoteDetailsFragment : ViewModelFragment<NoteDetailsViewModel>() {
    override val screenName: Int
        get() = R.string.title_note
    override val layout: Int
        get() = R.layout.fragment_note_detail
    override lateinit var viewModel: NoteDetailsViewModel
    private lateinit var baseViewModel: FormsViewModel
    private lateinit var noteContentAdapter: NoteDetailsAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = getViewModel()
        baseViewModel = getSharedViewModel(from = { requireParentFragment() })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setData(
            Parcels.unwrap<Note>(arguments?.getParcelable((Constants.NOTE))),
            baseViewModel.selectedQuestion().value
        )
        noteContentAdapter = NoteDetailsAdapter(requireContext())
        noteContent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = noteContentAdapter
            addItemDecoration(
                HorizontalDividerItemDecoration.Builder(requireContext())
                    .color(Color.TRANSPARENT)
                    .sizeResId(R.dimen.small_margin).build()
            )
        }

        viewModel.noteDetails.observe(viewLifecycleOwner, Observer {
            if (it.isSynced) {
                if (requireActivity().isOnline()) {
                    noteContentAdapter.updateAdapter(it)
                } else {
                    noteContentAdapter.updateAdapter(it.copy(attachedFiles = emptyList()))
                    Snackbar.make(
                        view,
                        getString(R.string.note_details_missing_internet),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } else {
                noteContentAdapter.updateAdapter(it)
            }
        })
    }

    companion object {
        val TAG = NoteDetailsFragment::class.java.simpleName
    }
}