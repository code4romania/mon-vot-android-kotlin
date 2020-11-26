package ro.code4.monitorizarevot.ui.forms

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.widget_change_polling_station_bar.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.parceler.Parcels
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.Constants.FORM
import ro.code4.monitorizarevot.helper.Constants.FORM_QUESTION_CODES
import ro.code4.monitorizarevot.helper.Constants.NOTE
import ro.code4.monitorizarevot.helper.Constants.QUESTION
import ro.code4.monitorizarevot.helper.changePollingStation
import ro.code4.monitorizarevot.helper.replaceFragment
import ro.code4.monitorizarevot.ui.base.ViewModelFragment
import ro.code4.monitorizarevot.ui.forms.questions.QuestionsDetailsFragment
import ro.code4.monitorizarevot.ui.forms.questions.QuestionsListFragment
import ro.code4.monitorizarevot.ui.main.MainActivity
import ro.code4.monitorizarevot.ui.notes.NoteDetailsFragment
import ro.code4.monitorizarevot.ui.notes.NoteFragment

class FormsFragment : ViewModelFragment<FormsViewModel>() {
    override val screenName: Int
        get() = R.string.menu_forms

    override val layout: Int
        get() = R.layout.fragment_main

    override val viewModel: FormsViewModel by viewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireFragmentManager().beginTransaction()
            .setPrimaryNavigationFragment(this)
            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.pollingStation().observe(viewLifecycleOwner, Observer {
            pollingStationBarText.text =
                getString(R.string.polling_station, it.pollingStationNumber, it.countyName)
        })

        viewModel.title().observe(viewLifecycleOwner, Observer {
            (activity as MainActivity).setTitle(it)
        })

        viewModel.selectedForm().observe(viewLifecycleOwner, Observer {
            childFragmentManager.replaceFragment(
                R.id.content,
                QuestionsListFragment(),
                bundleOf(Pair(FORM, Parcels.wrap(it))),
                QuestionsListFragment.TAG
            )
        })
        viewModel.selectedQuestion().observe(viewLifecycleOwner, Observer {
            childFragmentManager.replaceFragment(
                R.id.content,
                QuestionsDetailsFragment(),
                bundleOf(
                    Pair(FORM, Parcels.wrap(it.first)),
                    Pair(QUESTION, Parcels.wrap(it.second))
                ),
                QuestionsDetailsFragment.TAG
            )
        })
        viewModel.navigateToNotes().observe(viewLifecycleOwner, Observer {
            childFragmentManager.replaceFragment(
                R.id.content,
                NoteFragment(),
                it?.let { bundleOf(Pair(QUESTION, Parcels.wrap(it))) },
                NoteFragment.TAG
            )
        })

        viewModel.selectedNote().observe(viewLifecycleOwner, Observer {
            childFragmentManager.replaceFragment(
                R.id.content,
                NoteDetailsFragment(),
                bundleOf(
                    Pair(NOTE, Parcels.wrap(it.first)),
                    Pair(FORM_QUESTION_CODES, Parcels.wrap(it.second))
                ),
                NoteDetailsFragment.TAG
            )
        })

        pollingStationBarButton.setOnClickListener {
            viewModel.notifyChangeRequested()
            (activity as AppCompatActivity).changePollingStation()
        }
        childFragmentManager.replaceFragment(
            R.id.content,
            FormsListFragment()
        )

    }


}