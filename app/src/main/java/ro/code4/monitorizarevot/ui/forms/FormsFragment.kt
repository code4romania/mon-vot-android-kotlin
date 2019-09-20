package ro.code4.monitorizarevot.ui.forms

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_forms.*
import kotlinx.android.synthetic.main.widget_change_branch_bar.*
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.FormGridAdapter
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.ui.base.BaseFragment
import ro.code4.monitorizarevot.widget.SpacesItemDecoration

class FormsFragment : BaseFragment<FormsViewModel>() {
    override val layout: Int
        get() = R.layout.fragment_forms
    override val viewModel: FormsViewModel by viewModel()
    private lateinit var adapter: FormGridAdapter

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

        formsGrid.layoutManager = GridLayoutManager(mContext, 2)
        formsGrid.addItemDecoration(SpacesItemDecoration(mContext.resources.getDimensionPixelSize(R.dimen.small_margin)))

    }

    private fun setData(list: ArrayList<ListItem>) {
        if (!::adapter.isInitialized) {
            adapter = FormGridAdapter(mContext, list)
            formsGrid.adapter = adapter
        }
        //todo clear adapter when refreshing
    }

}