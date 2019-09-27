package ro.code4.monitorizarevot.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_form_section.view.*
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.adapters.helper.ViewHolder
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.pojo.FormWithSections
import ro.code4.monitorizarevot.helper.highlight

class FormAdapter(private val context: Context, private val items: ArrayList<ListItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var listener: OnClickListener

    companion object {
        const val TYPE_FORM = 0
        const val TYPE_NOTE = 1
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_form_section, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = items[position].type

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        val pair: Pair<CharSequence, Int> = when (item.type) {
            TYPE_FORM -> {

                val formWithSections = item.value as FormWithSections
                val noQuestions =
                    formWithSections.sections.fold(0, { acc, obj -> acc + obj.questions.size })
                holder.itemView.progress.max = noQuestions
                holder.itemView.progress.progress =
                    formWithSections.getNoAnsweredQuestions() //TODO change with synced questions
                holder.itemView.questionsAnswered.text = context.getString(
                    R.string.no_answered_questions,
                    formWithSections.getNoAnsweredQuestions(),
                    noQuestions
                )
                holder.itemView.progressGroup.visibility = View.VISIBLE
                with(formWithSections.form) {
                    holder.itemView.setOnClickListener { listener.onFormClick(this) }
                    val prefix = description
                    val suffix = context.getString(R.string.form_suffix, code)
                    Pair(context.highlight(prefix, suffix), getIcon(code))
                }

            }
            else -> {
                holder.itemView.setOnClickListener { listener.onNoteClick() }
                holder.itemView.progressGroup.visibility = View.INVISIBLE
                Pair(
                    context.highlight(context.getString(R.string.form_notes)),
                    R.drawable.ic_form_notes
                )
            }

        }
        holder.itemView.formDescription.text = pair.first
        holder.itemView.formIcon.setImageResource(pair.second)
    }

    private fun getIcon(code: String): Int {
        return when (code) {
            FormCode.A.name -> R.drawable.ic_form_a
            FormCode.B.name -> R.drawable.ic_form_b
            FormCode.C.name -> R.drawable.ic_form_c
            else -> R.drawable.ic_form_notes
        }
    }

    fun refreshData(list: java.util.ArrayList<ListItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    interface OnClickListener {
        fun onFormClick(form: FormDetails)
        fun onNoteClick()
    }

    enum class FormCode {
        A, B, C
    }
}