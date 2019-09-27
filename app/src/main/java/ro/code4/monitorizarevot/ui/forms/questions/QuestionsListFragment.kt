package ro.code4.monitorizarevot.ui.forms.questions

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list.*
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.parceler.Parcels
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.QuestionsAdapter
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.helper.Constants.FORM_CODE
import ro.code4.monitorizarevot.ui.base.BaseFragment
import ro.code4.monitorizarevot.ui.forms.FormsViewModel
import ro.code4.monitorizarevot.widget.SpacesItemDecoration

class QuestionsListFragment : BaseFragment<QuestionsViewModel>(), QuestionsAdapter.OnClickListener {


    override val layout: Int
        get() = R.layout.fragment_list
    override val viewModel: QuestionsViewModel by viewModel()
    lateinit var baseViewModel: FormsViewModel
    private lateinit var adapter: QuestionsAdapter

    companion object {
        val TAG = QuestionsListFragment::class.java.simpleName
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseViewModel = getSharedViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.questions().observe(this, Observer {
            setData(it)
        })
        viewModel.setData(Parcels.unwrap<FormDetails>(arguments?.getParcelable((FORM_CODE))))
//        viewModel.getQuestions(arguments?.getString(FORM_CODE, ""))
        list.layoutManager = LinearLayoutManager(mContext)
        list.addItemDecoration(SpacesItemDecoration(mContext.resources.getDimensionPixelSize(R.dimen.margin)))

    }

    private fun setData(items: ArrayList<ListItem>) {
        if (!::adapter.isInitialized) {
            adapter = QuestionsAdapter(mContext, items)
            adapter.listener = this
            list.adapter = adapter
        } else {
            adapter.refreshData(items)
        }
    }

    override fun onQuestionClick(question: Question) {

    }


}