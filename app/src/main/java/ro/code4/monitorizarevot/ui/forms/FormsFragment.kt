package ro.code4.monitorizarevot.ui.forms

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.widget_change_branch_bar.*
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.changeBranch
import ro.code4.monitorizarevot.helper.replaceFragment
import ro.code4.monitorizarevot.ui.base.BaseFragment

class FormsFragment : BaseFragment<FormsViewModel>() {


    override val layout: Int
        get() = R.layout.fragment_main
    override val viewModel: FormsViewModel by viewModel()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.branchBarText().observe(this, Observer {
            branchBarText.text = it
        })


        viewModel.getBranchBarText()

        branchBarButton.setOnClickListener {
            (activity as AppCompatActivity).changeBranch()
        }
        childFragmentManager.replaceFragment(R.id.content, FormsListFragment())

    }


}