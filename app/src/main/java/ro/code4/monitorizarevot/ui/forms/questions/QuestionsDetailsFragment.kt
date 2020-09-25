package ro.code4.monitorizarevot.ui.forms.questions

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import kotlinx.android.synthetic.main.fragment_question_details.*
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.parceler.Parcels
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.QuestionDetailsAdapter
import ro.code4.monitorizarevot.adapters.helper.QuestionDetailsListItem
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.helper.Constants
import ro.code4.monitorizarevot.helper.addOnLayoutChangeListenerForGalleryEffect
import ro.code4.monitorizarevot.helper.addOnScrollListenerForGalleryEffect
import ro.code4.monitorizarevot.ui.base.ViewModelFragment
import ro.code4.monitorizarevot.ui.forms.FormsViewModel


class QuestionsDetailsFragment : ViewModelFragment<QuestionsDetailsViewModel>(),
    QuestionDetailsAdapter.OnClickListener {
    override fun addNoteFor(question: Question) {
        baseViewModel.selectedNotes(question)
    }

    override val layout: Int
        get() = R.layout.fragment_question_details
    override val screenName: Int
        get() = R.string.analytics_title_question

    override val viewModel: QuestionsDetailsViewModel by viewModel()
    private lateinit var baseViewModel: FormsViewModel
    private lateinit var adapter: QuestionDetailsAdapter
    private var currentPosition: Int = 0
    private lateinit var layoutManager: LinearLayoutManager
    private var recyclerViewState: Parcelable? = null

    companion object {
        val TAG = QuestionsDetailsFragment::class.java.simpleName
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseViewModel = getSharedViewModel(from = { parentFragment!! })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.questions().observe(this, Observer { list ->
            setData(ArrayList(list.map { it as QuestionDetailsListItem }))
        })

        viewModel.title().observe(this, Observer {
            baseViewModel.setTitle(it)
        })

        viewModel.setData(Parcels.unwrap<FormDetails>(arguments?.getParcelable((Constants.FORM))))
        layoutManager = LinearLayoutManager(mContext, HORIZONTAL, false)
        list.layoutManager = layoutManager

        list.addOnScrollListenerForGalleryEffect()
        list.addOnLayoutChangeListenerForGalleryEffect()
        list.itemAnimator = null
        list.setHasFixedSize(true)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(list)
        nextQuestionBtn.setOnClickListener {
            root.requestFocus()
            if (currentPosition < adapter.itemCount - 1) {
                list.smoothScrollToPosition(currentPosition + 1)
            } else {
                activity?.onBackPressed()
            }
        }
        previousQuestionBtn.setOnClickListener {
            root.requestFocus()
            if (currentPosition > 0) {
                list.smoothScrollToPosition(currentPosition - 1)
            }
        }

        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    snapHelper.findSnapView(layoutManager)?.also {
                        val oldPos = currentPosition
                        currentPosition = layoutManager.getPosition(it)
                        val (start, end) = if (oldPos < currentPosition) {
                            Pair(oldPos, currentPosition - 1)
                        } else {
                            Pair(currentPosition + 1, oldPos)
                        }
                        for (pos in start..end) {
                            viewModel.saveAnswer(adapter.getItem(pos))
                        }
                    }
                    setButtons()
                }
            }

        })

    }

    private fun setButtons() {
        val (previousBtnVisibility, nextBtnResId) = when {
            currentPosition == 0 && adapter.itemCount - 1 > 0 -> Pair(
                View.GONE,
                R.string.question_next
            )
            currentPosition == 0 && adapter.itemCount - 1 == 0 -> Pair(
                View.GONE,
                R.string.question_finish
            )
            currentPosition == adapter.itemCount - 1 -> Pair(View.VISIBLE, R.string.question_finish)
            else -> Pair(View.VISIBLE, R.string.question_next)

        }
        previousQuestionBtn.visibility = previousBtnVisibility
        nextQuestionBtn.text = getString(nextBtnResId)
    }

    private fun setData(items: ArrayList<QuestionDetailsListItem>) {
        if (!::adapter.isInitialized) {
            adapter = QuestionDetailsAdapter(mContext, items)
            adapter.listener = this
            currentPosition = items.indexOfFirst {
                it.questionWithAnswers.question.id == Parcels.unwrap<Question>(
                    arguments?.getParcelable(
                        (Constants.QUESTION)
                    )
                ).id
            }
            layoutManager.scrollToPosition(currentPosition)

        } else {
            recyclerViewState = list.layoutManager?.onSaveInstanceState()
            adapter.submitList(items)
        }

        list.adapter = adapter
        recyclerViewState?.let {
            list.layoutManager?.onRestoreInstanceState(it)
        }
        setButtons()
    }

    override fun onPause() {
        if (::adapter.isInitialized) {
            viewModel.saveAnswer(adapter.getItem(currentPosition))
        }
        viewModel.syncData()
        super.onPause()

    }
}