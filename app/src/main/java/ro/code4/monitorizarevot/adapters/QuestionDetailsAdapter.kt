package ro.code4.monitorizarevot.adapters

import android.content.Context
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_question_details.view.*
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.helper.ViewHolder
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.data.pojo.QuestionWithAnswers
import ro.code4.monitorizarevot.helper.TextWatcherDelegate
import ro.code4.monitorizarevot.widget.AnswerRadioGroup
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

    override fun getItemId(position: Int): Long = items[position].question.id.toLong()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_question_details, parent, false)

        when (viewType) {
            TYPE_SINGLE_CHOICE, TYPE_SINGLE_CHOICE_DETAILS -> {
                val radioGroup = AnswerRadioGroup(context)
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
            when {
                synced -> {
                    holder.itemView.syncIcon.visibility = View.VISIBLE
                    holder.itemView.syncIcon.setImageResource(R.drawable.ic_synced)
                    holder.itemView.syncText.visibility = View.INVISIBLE
                }
                savedLocally && !synced -> {
                    holder.itemView.syncIcon.visibility = View.VISIBLE
                    holder.itemView.syncText.visibility = View.VISIBLE
                    holder.itemView.syncIcon.setImageResource(R.drawable.ic_sync_progress)
                }
                else -> {
                    holder.itemView.syncIcon.visibility = View.INVISIBLE
                    holder.itemView.syncText.visibility = View.INVISIBLE
                }
            }
            holder.itemView.questionCode.text = code
            holder.itemView.question.text = text
        }

    }

    private fun setupSingleChoice(holder: RecyclerView.ViewHolder, item: QuestionWithAnswers) {
        holder.itemView.answersLayout.findViewById<AnswerRadioGroup>(R.id.answersRadioGroup)
            .removeAllViews()
        item.answers.forEach {
            val checkedChangedListener =
                CompoundButton.OnCheckedChangeListener { p0, p1 ->
                    it.selected = p1
                    holder.itemView.answersLayout.findViewById<AnswerRadioGroup>(R.id.answersRadioGroup)
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
            holder.itemView.findViewById<AnswerRadioGroup>(R.id.answersRadioGroup)
                .addView(view, params)
        }
    }

    private fun setupMultiChoice(holder: RecyclerView.ViewHolder, item: QuestionWithAnswers) {
        holder.itemView.answersLayout.removeAllViews()
        item.answers.forEach {
            val checkedChangedListener =
                CompoundButton.OnCheckedChangeListener { _, p1 ->
                    it.selected = p1
                }
            val textWatcher = object : TextWatcher by TextWatcherDelegate {
                override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                    it.value = p0.toString()
                }
            }
            val view: View = if (it.hasManualInput) {
                CheckBoxWithDetails(context).apply {
                    setText(it.text)
                    setTextChangedListener(textWatcher)
                    setCheckedChangedListener(checkedChangedListener)
                    setValue(it.value)
                }
            } else {
                AppCompatCheckBox(ContextThemeWrapper(context, R.style.CheckBox), null, 0).apply {
                    text = it.text
                    setOnCheckedChangeListener(checkedChangedListener)
                }
            }
            (view as Checkable).isChecked = it.selected
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

    fun getItem(position: Int): QuestionWithAnswers = items[position]

    interface OnClickListener {
        fun onQuestionClick(question: Question)
    }
}