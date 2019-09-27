package ro.code4.monitorizarevot.ui.forms

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_forms.*
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.FormAdapter
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.ui.base.BaseFragment
import ro.code4.monitorizarevot.widget.SpacesItemDecoration


class FormsListFragment : BaseFragment<FormsViewModel>(), FormAdapter.OnClickListener {

    companion object {
        val TAG = FormsListFragment::class.java.simpleName
    }
    override val layout: Int
        get() = R.layout.fragment_forms
    override lateinit var viewModel: FormsViewModel
    private lateinit var adapter: FormAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = getSharedViewModel(from = { parentFragment!! })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.forms().observe(this, Observer {
            setData(it)
        })
        formsList.layoutManager = LinearLayoutManager(mContext)
        formsList.addItemDecoration(SpacesItemDecoration(mContext.resources.getDimensionPixelSize(R.dimen.small_margin)))

    }

    private fun setData(list: ArrayList<ListItem>) {
        if (!::adapter.isInitialized) {
            adapter = FormAdapter(mContext, list)
            adapter.listener = this

        } else {
            adapter.refreshData(list)
        }
        formsList.adapter = adapter
    }

    override fun onFormClick(form: FormDetails) {
        viewModel.selectForm(form)
    }

    override fun onNoteClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates. //go to add note
    }


}