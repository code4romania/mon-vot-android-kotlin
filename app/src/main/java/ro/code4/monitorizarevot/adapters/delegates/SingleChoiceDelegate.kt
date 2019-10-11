package ro.code4.monitorizarevot.adapters.delegates

import android.content.Context
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_question_details.*
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.helper.QuestionDetailsListItem
import ro.code4.monitorizarevot.adapters.helper.SingleChoiceListItem
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.data.pojo.QuestionWithAnswers
import ro.code4.monitorizarevot.helper.TextWatcherDelegate
import ro.code4.monitorizarevot.widget.AnswerRadioGroup
import ro.code4.monitorizarevot.widget.RadioButtonWithDetails

class SingleChoiceDelegate(
    private val clickListener: (Question) -> Unit
) : AbsListItemAdapterDelegate<SingleChoiceListItem, QuestionDetailsListItem, SingleChoiceDelegate.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_question_details, parent, false)

        val radioGroup = AnswerRadioGroup(parent.context)
        radioGroup.orientation = VERTICAL
        radioGroup.id = R.id.answersRadioGroup
        view.findViewById<LinearLayout>(R.id.answersLayout).addView(radioGroup)

        return ViewHolder(parent.context, view, clickListener)
    }

    override fun isForViewType(
        item: QuestionDetailsListItem,
        items: MutableList<QuestionDetailsListItem>,
        position: Int
    ): Boolean = item is SingleChoiceListItem

    override fun onBindViewHolder(
        item: SingleChoiceListItem,
        holder: ViewHolder,
        payloads: MutableList<Any>
    ) {
        holder.bind(item.questionWithAnswers)
    }

    class ViewHolder(
        val context: Context,
        override val containerView: View,
        clickListener: (Question) -> Unit
    ) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        private var params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = context.resources.getDimensionPixelSize(R.dimen.small_margin)
        }
        lateinit var questionWithAnswers: QuestionWithAnswers

        init {
            addNoteButton.setOnClickListener { clickListener(questionWithAnswers.question) }
        }

        fun bind(item: QuestionWithAnswers) {
            questionWithAnswers = item

            containerView.findViewById<AnswerRadioGroup>(R.id.answersRadioGroup)
                .removeAllViews()
            questionWithAnswers.answers?.forEach {
                val checkedChangedListener =
                    CompoundButton.OnCheckedChangeListener { p0, p1 ->
                        questionWithAnswers.question.synced =
                            it.selected == p1 && questionWithAnswers.question.synced
                        questionWithAnswers.question.savedLocally =
                            it.selected == p1 && questionWithAnswers.question.savedLocally
                        it.selected = p1
                        answersLayout.findViewById<AnswerRadioGroup>(R.id.answersRadioGroup)
                            .onCheckedChanged(p0, p1)
                    }
                val textWatcher = object : TextWatcher by TextWatcherDelegate {
                    override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                        it.value = p0.toString()
                    }
                }
                val view: View = if (it.hasManualInput) {
                    RadioButtonWithDetails(context).apply {
                        setText(it.text)
                        setValue(it.value)
                        setTextChangedListener(textWatcher)
                        setCheckedChangedListener(checkedChangedListener)
                    }
                } else {
                    AppCompatRadioButton(
                        ContextThemeWrapper(context, R.style.RadioButton),
                        null,
                        0
                    ).apply {
                        text = it.text
                        setOnCheckedChangeListener(checkedChangedListener)
                    }

                }
                (view as Checkable).isChecked = it.selected
                view.tag = it.id
                containerView.findViewById<AnswerRadioGroup>(R.id.answersRadioGroup)
                    .addView(view, params)
            }

            with(questionWithAnswers.question) {
                Log.i("gaga", "$text $synced $savedLocally")
                val (syncIconVisibility, syncTextVisibility, syncIconResource) = when {
                    synced -> arrayOf(View.VISIBLE, View.INVISIBLE, R.drawable.ic_synced)
                    savedLocally && !synced -> arrayOf(
                        View.VISIBLE,
                        View.VISIBLE,
                        R.drawable.ic_sync_progress
                    )
                    else -> arrayOf(View.INVISIBLE, View.INVISIBLE, null)
                }

                syncText.visibility = syncTextVisibility ?: View.INVISIBLE
                syncIcon.visibility = syncIconVisibility ?: View.INVISIBLE
                syncIconResource?.let { syncIcon.setImageResource(it) }
                questionCode.text = code
                question.text = text
            }

        }
    }
}