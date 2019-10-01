package ro.code4.monitorizarevot.ui.forms.questions

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import kotlinx.android.synthetic.main.fragment_question_details.*
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.parceler.Parcels
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.QuestionDetailsAdapter
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.pojo.QuestionWithAnswers
import ro.code4.monitorizarevot.helper.Constants
import ro.code4.monitorizarevot.helper.addOnLayoutChangeListenerForGalleryEffect
import ro.code4.monitorizarevot.helper.addOnScrollListenerForGalleryEffect
import ro.code4.monitorizarevot.ui.base.BaseFragment
import ro.code4.monitorizarevot.ui.forms.FormsViewModel


class QuestionsDetailsFragment : BaseFragment<QuestionsDetailsViewModel>() {


    override val layout: Int
        get() = R.layout.fragment_question_details
    override val viewModel: QuestionsDetailsViewModel by viewModel()
    private lateinit var baseViewModel: FormsViewModel
    private lateinit var adapter: QuestionDetailsAdapter

    companion object {
        val TAG = QuestionsDetailsFragment::class.java.simpleName
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
        viewModel.setData(Parcels.unwrap<FormDetails>(arguments?.getParcelable((Constants.FORM))))
        list.layoutManager = LinearLayoutManager(mContext, HORIZONTAL, false)
//        list.addItemDecoration(
//            VerticalDividerItemDecoration.Builder(activity)
//                .color(Color.TRANSPARENT)
//                .sizeResId(R.dimen.small_margin).build()
//        )
        list.addOnScrollListenerForGalleryEffect()
        list.addOnLayoutChangeListenerForGalleryEffect()

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(list)


    }

    private fun setData(items: ArrayList<QuestionWithAnswers>) {
        if (!::adapter.isInitialized) {
            adapter = QuestionDetailsAdapter(mContext, items)
//            adapter.listener = this
            list.adapter = adapter
        } else {
            adapter.refreshData(items)
        }
    }


}