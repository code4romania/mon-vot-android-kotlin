package ro.code4.monitorizarevot.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_question.view.*
import kotlinx.android.synthetic.main.item_section.view.*
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.adapters.helper.ViewHolder
import ro.code4.monitorizarevot.data.model.Question
import ro.code4.monitorizarevot.data.model.Section
import ro.code4.monitorizarevot.data.pojo.QuestionWithAnswers

class QuestionsAdapter(private val context: Context, private val items: ArrayList<ListItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var listener: OnClickListener

    companion object {
        const val TYPE_QUESTION = 0
        const val TYPE_SECTION = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val layout = when (viewType) {
            TYPE_QUESTION -> R.layout.item_question
            else -> R.layout.item_section
        }
        val view = LayoutInflater.from(context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = items[position]
        when (item.type) {
            TYPE_QUESTION -> {

                val questionWithAnswers = item.value as QuestionWithAnswers
                with(questionWithAnswers.question) {
                    if (savedLocally || synced) {
                        holder.itemView.syncIcon.visibility = View.VISIBLE
                        holder.itemView.syncIcon.setImageResource(if (synced) R.drawable.ic_synced else R.drawable.ic_sync_progress)
                    } else {
                        holder.itemView.syncIcon.visibility = View.INVISIBLE
                    }
                    holder.itemView.questionCode.text = code
                    holder.itemView.question.text = text
                    holder.itemView.setOnClickListener {
                        listener.onQuestionClick(this)
                    }
                }

            }
            else -> {
                holder.itemView.setOnClickListener(null)
                val section = item.value as Pair<Int, Section>

                holder.itemView.sectionName.text = context.getString(
                    R.string.section_title,
                    section.first,
                    section.second.description
                )
            }

        }

    }

    override fun getItemViewType(position: Int): Int = items[position].type

    fun refreshData(list: ArrayList<ListItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }


    interface OnClickListener {
        fun onQuestionClick(question: Question)
    }

}