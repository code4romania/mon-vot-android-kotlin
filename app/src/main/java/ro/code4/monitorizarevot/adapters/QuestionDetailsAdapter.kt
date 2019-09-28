package ro.code4.monitorizarevot.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.RadioGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_question_details.view.*
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.helper.ViewHolder
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.data.pojo.QuestionWithAnswers
import ro.code4.monitorizarevot.widget.CheckBoxWithDetails
import ro.code4.monitorizarevot.widget.RadioButtonWithDetails


class QuestionDetailsAdapter(
    private val context: Context,
    private val items: ArrayList<QuestionWithAnswers>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply {
        bottomMargin = context.resources.getDimensionPixelSize(R.dimen.small_margin)
    }
    lateinit var listener: OnClickListener

    companion object {
        const val TYPE_MULTI_CHOICE = 0
        const val TYPE_SINGLE_CHOICE = 1
        const val TYPE_SINGLE_CHOICE_DETAILS = 2
        const val TYPE_MULTI_CHOICE_DETAILS = 3
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_question_details, parent, false)

        when (viewType) {
            TYPE_SINGLE_CHOICE, TYPE_SINGLE_CHOICE_DETAILS -> {
                val radioGroup = RadioGroup(context)
                radioGroup.orientation = VERTICAL
                radioGroup.id = R.id.answersRadioGroup
                view.findViewById<LinearLayout>(R.id.answersLayout).addView(radioGroup)
            }
        }

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (getItemViewType(position)) {
            TYPE_MULTI_CHOICE, TYPE_MULTI_CHOICE_DETAILS -> setupMultiChoice(holder, item)
            TYPE_SINGLE_CHOICE, TYPE_SINGLE_CHOICE_DETAILS -> setupSingleChoice(holder, item)
        }
        with(item.question) {
            holder.itemView.questionCode.text = code
            holder.itemView.question.text = text
        }

    }

    private fun setupSingleChoice(holder: RecyclerView.ViewHolder, item: QuestionWithAnswers) {
        item.answers.forEach {
            val view: View = if (it.hasManualInput) {
                RadioButtonWithDetails(context).apply {
                    setText(it.text)
                }
            } else {
                AppCompatRadioButton(
                    ContextThemeWrapper(context, R.style.RadioButton),
                    null,
                    0
                ).apply {
                    text = it.text
                }

            }
            view.tag = it.id
            holder.itemView.findViewById<RadioGroup>(R.id.answersRadioGroup)
                .addView(view, params)
        }
    }

    private fun setupMultiChoice(holder: RecyclerView.ViewHolder, item: QuestionWithAnswers) {

        item.answers.forEach {
            val view: View = if (it.hasManualInput) {
                CheckBoxWithDetails(context).apply {
                    setText(it.text)
                }
            } else {
                AppCompatCheckBox(ContextThemeWrapper(context, R.style.CheckBox), null, 0).apply {
                    text = it.text
                }
            }
            view.tag = it.id
            holder.itemView.answersLayout.addView(view, params)

        }
    }

    override fun getItemViewType(position: Int): Int = items[position].question.questionType

    fun refreshData(list: ArrayList<QuestionWithAnswers>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    interface OnClickListener {
        fun onQuestionClick(question: Question)
    }
}