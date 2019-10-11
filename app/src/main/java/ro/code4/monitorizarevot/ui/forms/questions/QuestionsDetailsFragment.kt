package ro.code4.monitorizarevot.ui.forms.questions

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
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
import ro.code4.monitorizarevot.adapters.QuestionDetailsDelegationAdapter
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.adapters.helper.QuestionDetailsListItem
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.model.Question
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
    private val questionDetailsAdapter: QuestionDetailsDelegationAdapter by lazy {
        QuestionDetailsDelegationAdapter(baseViewModel::selectedNotes)
    }
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

        viewModel.questions().observe(this, Observer { it ->
            Log.i("gaga", "new list")
            setData(it)
        })

        viewModel.title().observe(this, Observer {
            baseViewModel.setTitle(it)
        })

        viewModel.setData(Parcels.unwrap<FormDetails>(arguments?.getParcelable((Constants.FORM))))
        layoutManager = LinearLayoutManager(mContext, HORIZONTAL, false)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(list)
        with(list) {
            layoutManager = this@QuestionsDetailsFragment.layoutManager
            addOnScrollListenerForGalleryEffect()
            addOnLayoutChangeListenerForGalleryEffect()
            itemAnimator = null
            adapter = questionDetailsAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        snapHelper.findSnapView(layoutManager)?.also {
                            val oldPos = currentPosition
                            currentPosition =
                                this@QuestionsDetailsFragment.layoutManager.getPosition(it)
                            val (start, end) = if (oldPos < currentPosition) {
                                Pair(oldPos, currentPosition - 1)
                            } else {
                                Pair(currentPosition + 1, oldPos)
                            }
                            for (pos in start..end) {
                                Log.i("gaga", "$currentPosition $pos")
                                viewModel.saveAnswer(questionDetailsAdapter.getItem(pos))
                            }
                        }
                        setVisibilityOnButtons()
                    }
                }

            })
        }

        nextQuestionBtn.setOnClickListener {
            root.requestFocus()
            if (currentPosition < questionDetailsAdapter.itemCount - 1) {//todo check if you can remove this, in theory the button shouldn't be visible when currentPosition ==  adapter.itemCount - 1
                list.smoothScrollToPosition(currentPosition + 1)
            }
        }
        previousQuestionBtn.setOnClickListener {
            root.requestFocus()
            if (currentPosition > 0) { //todo check if you can remove this, in theory the button shouldn't be visible when currentPosition == 0
                list.smoothScrollToPosition(currentPosition - 1)
            }
        }

    }

    private fun setData(items: ArrayList<ListItem>) {
        val questions = items.map { it as QuestionDetailsListItem }
        val oldCount = questionDetailsAdapter.itemCount

        if (oldCount == 0) {
            questionDetailsAdapter.items = questions
            currentPosition = questions.indexOfFirst {
                it.questionWithAnswers.question.id == Parcels.unwrap<Question>(
                    arguments?.getParcelable(
                        (Constants.QUESTION)
                    )
                ).id
            }
            layoutManager.scrollToPosition(currentPosition)
            setVisibilityOnButtons()
        } else {
            recyclerViewState = list.layoutManager?.onSaveInstanceState()
            questionDetailsAdapter.items = questions
        }
        recyclerViewState?.let {
            list.layoutManager?.onRestoreInstanceState(it)
        }
    }

    private fun setVisibilityOnButtons() {
        when (currentPosition) {
            0 -> previousQuestionBtn.visibility = View.GONE
            questionDetailsAdapter.itemCount - 1 -> nextQuestionBtn.visibility = View.GONE
            else -> {
                previousQuestionBtn.visibility = View.VISIBLE
                nextQuestionBtn.visibility = View.VISIBLE
            }
        }
    }

    override fun onPause() {
        viewModel.saveAnswer(questionDetailsAdapter.getItem(currentPosition))
        viewModel.syncData()
        super.onPause()

    }

    override fun onResume() {
        super.onResume()
        setVisibilityOnButtons()
    }
}