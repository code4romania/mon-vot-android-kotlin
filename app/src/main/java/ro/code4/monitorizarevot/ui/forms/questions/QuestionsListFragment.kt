package ro.code4.monitorizarevot.ui.forms.questions

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_list.*
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.parceler.Parcels
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.QuestionsAdapter
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.helper.Constants.FORM
import ro.code4.monitorizarevot.ui.base.BaseFragment
import ro.code4.monitorizarevot.ui.forms.FormsViewModel

class QuestionsListFragment : BaseFragment<QuestionsViewModel>(), QuestionsAdapter.OnClickListener {


    override val layout: Int
        get() = R.layout.fragment_list
    override val viewModel: QuestionsViewModel by viewModel()
    private lateinit var baseViewModel: FormsViewModel
    private lateinit var adapter: QuestionsAdapter

    companion object {
        val TAG = QuestionsListFragment::class.java.simpleName
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseViewModel = getSharedViewModel(from = { parentFragment!! })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.questions().observe(this, Observer {
            setData(it)
        })
        viewModel.setData(Parcels.unwrap<FormDetails>(arguments?.getParcelable((FORM))))
//        viewModel.getQuestions(arguments?.getString(FORM_CODE, ""))
        list.layoutManager = LinearLayoutManager(mContext)
        list.addItemDecoration(
            HorizontalDividerItemDecoration.Builder(activity)
                .color(Color.TRANSPARENT)
                .sizeResId(R.dimen.margin).build()
        )

    }

    private fun setData(items: ArrayList<ListItem>) {
        if (!::adapter.isInitialized) {
            adapter = QuestionsAdapter(mContext, items)
            adapter.listener = this
        } else {
            adapter.refreshData(items)
        }
        list.adapter = adapter
    }

    override fun onQuestionClick(question: Question) {
        baseViewModel.selectQuestion(question)
    }


}