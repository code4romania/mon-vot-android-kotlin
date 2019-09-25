package ro.code4.monitorizarevot.ui.forms

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_forms.*
import kotlinx.android.synthetic.main.widget_change_branch_bar.*
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.FormAdapter
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.helper.changeBranch
import ro.code4.monitorizarevot.ui.base.BaseFragment
import ro.code4.monitorizarevot.widget.SpacesItemDecoration

class FormsFragment : BaseFragment<FormsViewModel>(), FormAdapter.OnClickListener {


    override val layout: Int
        get() = R.layout.fragment_forms
    override val viewModel: FormsViewModel by viewModel()
    private lateinit var adapter: FormAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.branchBarText().observe(this, Observer {
            branchBarText.text = it
        })

        viewModel.forms().observe(this, Observer {
            setData(it)
        })
        viewModel.getBranchBarText()
        viewModel.getForms()
        branchBarButton.setOnClickListener {
            (activity as AppCompatActivity).changeBranch()
        }
        formsList.layoutManager = LinearLayoutManager(mContext)
        formsList.addItemDecoration(SpacesItemDecoration(mContext.resources.getDimensionPixelSize(R.dimen.small_margin)))

    }

    private fun setData(list: ArrayList<ListItem>) {
        if (!::adapter.isInitialized) {
            adapter = FormAdapter(mContext, list)
            adapter.listener = this
            formsList.adapter = adapter
        }
        //todo clear adapter when refreshing
    }

    override fun onFormClick(form: FormDetails) {

    }

    override fun onNoteClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates. //go to add note
    }


}