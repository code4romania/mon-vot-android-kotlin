package ro.code4.monitorizarevot.ui.forms

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.widget_change_branch_bar.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.parceler.Parcels
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.Constants.FORM
import ro.code4.monitorizarevot.helper.Constants.QUESTION
import ro.code4.monitorizarevot.helper.changeBranch
import ro.code4.monitorizarevot.helper.replaceFragment
import ro.code4.monitorizarevot.ui.base.BaseFragment
import ro.code4.monitorizarevot.ui.forms.questions.QuestionsDetailsFragment
import ro.code4.monitorizarevot.ui.forms.questions.QuestionsListFragment
import ro.code4.monitorizarevot.ui.notes.NoteFragment

class FormsFragment : BaseFragment<FormsViewModel>() {


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
        viewModel.branchBarText().observe(this, Observer {
            branchBarText.text = it
        })

        viewModel.selectedForm().observe(this, Observer {
            childFragmentManager.replaceFragment(
                R.id.content,
                QuestionsListFragment(),
                bundleOf(Pair(FORM, Parcels.wrap(it))),
                QuestionsListFragment.TAG
            )
        })
        viewModel.selectedQuestion().observe(this, Observer {
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
        viewModel.navigateToNotes().observe(this, Observer {
            childFragmentManager.replaceFragment(
                R.id.content,
                NoteFragment(),
                bundleOf(
                    Pair(QUESTION, Parcels.wrap(it))
                ),
                QuestionsDetailsFragment.TAG
            )
        })
        viewModel.getBranchBarText()

        branchBarButton.setOnClickListener {
            (activity as AppCompatActivity).changeBranch()
        }
        childFragmentManager.replaceFragment(
            R.id.content,
            FormsListFragment()
        )

    }


}