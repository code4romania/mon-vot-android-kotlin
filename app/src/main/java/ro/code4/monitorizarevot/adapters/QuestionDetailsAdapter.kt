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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_question_details.view.*
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.helper.QuestionDetailsListItem
import ro.code4.monitorizarevot.adapters.helper.ViewHolder
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.data.pojo.QuestionWithAnswers
import ro.code4.monitorizarevot.helper.Constants.TYPE_MULTI_CHOICE
import ro.code4.monitorizarevot.helper.Constants.TYPE_MULTI_CHOICE_DETAILS
import ro.code4.monitorizarevot.helper.Constants.TYPE_SINGLE_CHOICE
import ro.code4.monitorizarevot.helper.Constants.TYPE_SINGLE_CHOICE_DETAILS
import ro.code4.monitorizarevot.helper.TextWatcherDelegate
import ro.code4.monitorizarevot.widget.AnswerRadioGroup
import ro.code4.monitorizarevot.widget.CheckBoxWithDetails
import ro.code4.monitorizarevot.widget.RadioButtonWithDetails


class QuestionDetailsAdapter constructor(
    private val context: Context,
    private val items: ArrayList<QuestionDetailsListItem>
) : ListAdapter<QuestionDetailsListItem, ViewHolder>(DIFF_CALLBACK) {


    private var params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply {
        bottomMargin = context.resources.getDimensionPixelSize(R.dimen.small_margin)
    }
    lateinit var listener: OnClickListener

    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<QuestionDetailsListItem>() {
            override fun areItemsTheSame(
                oldItem: QuestionDetailsListItem,
                newItem: QuestionDetailsListItem
            ): Boolean =
                oldItem.questionWithAnswers.question.id == newItem.questionWithAnswers.question.id


            override fun areContentsTheSame(
                oldItem: QuestionDetailsListItem,
                newItem: QuestionDetailsListItem
            ): Boolean =
                oldItem.questionWithAnswers == newItem.questionWithAnswers

        }
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long =
        items[position].questionWithAnswers.question.id.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        when (getItemViewType(position)) {
            TYPE_MULTI_CHOICE, TYPE_MULTI_CHOICE_DETAILS -> setupMultiChoice(
                holder,
                item.questionWithAnswers
            )
            TYPE_SINGLE_CHOICE, TYPE_SINGLE_CHOICE_DETAILS -> setupSingleChoice(
                holder,
                item.questionWithAnswers
            )
        }
        with(item.questionWithAnswers.question) {
            when {
                synced -> {
                    holder.itemView.syncIcon.visibility = View.VISIBLE
                    holder.itemView.syncIcon.setImageResource(R.drawable.ic_synced)
                    holder.itemView.syncText.visibility = View.INVISIBLE
                }
                savedLocally && !synced -> {
                    holder.itemView.syncIcon.visibility = View.VISIBLE
                    holder.itemView.syncText.visibility = View.VISIBLE
                    holder.itemView.syncIcon.setImageResource(R.drawable.ic_sync_not_done)
                }
                else -> {
                    holder.itemView.syncIcon.visibility = View.INVISIBLE
                    holder.itemView.syncText.visibility = View.INVISIBLE
                }
            }
            holder.itemView.questionCode.text = code
            holder.itemView.question.text = text
            holder.itemView.addNoteButton.setOnClickListener {
                listener.addNoteFor(this)
            }
        }

    }

    private fun setupSingleChoice(holder: RecyclerView.ViewHolder, item: QuestionWithAnswers) {
        holder.itemView.answersLayout.findViewById<AnswerRadioGroup>(R.id.answersRadioGroup)
            .removeAllViews()
        item.answers?.forEach {
            val checkedChangedListener =
                CompoundButton.OnCheckedChangeListener { p0, p1 ->
                    item.question.synced = it.selected == p1 && item.question.synced
                    it.selected = p1
                    holder.itemView.answersLayout.findViewById<AnswerRadioGroup>(R.id.answersRadioGroup)
                        .onCheckedChanged(p0, p1)
                }
            val textWatcher = object : TextWatcher by TextWatcherDelegate {
                override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                    it.value = p0.toString()
                }
            }
            val view: View = if (it.isFreeText) {
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
            view.tag = it.idOption
            holder.itemView.findViewById<AnswerRadioGroup>(R.id.answersRadioGroup)
                .addView(view, params)
        }
    }

    private fun setupMultiChoice(holder: RecyclerView.ViewHolder, item: QuestionWithAnswers) {
        holder.itemView.answersLayout.removeAllViews()
        item.answers?.forEach {
            val checkedChangedListener =
                CompoundButton.OnCheckedChangeListener { _, p1 ->
                    item.question.synced = it.selected == p1 && item.question.synced
                    it.selected = p1
                }
            val textWatcher = object : TextWatcher by TextWatcherDelegate {
                override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                    it.value = p0.toString()
                }
            }
            val view: View = if (it.isFreeText) {
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
            view.tag = it.idOption
            holder.itemView.answersLayout.addView(view, params)

        }
    }


    override fun getItemViewType(position: Int): Int =
        items[position].questionWithAnswers.question.questionType

    public override fun getItem(position: Int): QuestionDetailsListItem = items[position]

    interface OnClickListener {
        fun addNoteFor(question: Question)
    }
}