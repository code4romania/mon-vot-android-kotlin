package ro.code4.monitorizarevot.adapters.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_form_section.*
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.helper.FormListItem
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.model.FormDetails
import ro.code4.monitorizarevot.data.pojo.FormWithSections
import ro.code4.monitorizarevot.helper.highlight

class FormDelegate(
    private val clickListener: (FormDetails) -> Unit
) : AbsListItemAdapterDelegate<FormListItem, ListItem, FormDelegate.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_form_section, parent, false),
            clickListener
        )

    override fun isForViewType(item: ListItem, items: MutableList<ListItem>, position: Int): Boolean =
        item is FormListItem

    override fun onBindViewHolder(
        item: FormListItem,
        holder: ViewHolder,
        payloads: MutableList<Any>
    ) {
        holder.bind(item.formWithSections)
    }

    class ViewHolder(override val containerView: View, clickListener: (FormDetails) -> Unit) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        private lateinit var item: FormWithSections

        init {
            containerView.setOnClickListener { clickListener(item.form) }
        }

        fun bind(formWithSections: FormWithSections) {
            item = formWithSections


            val noQuestions =
                formWithSections.sections.fold(0, { acc, obj -> acc + obj.questions.size })
            progress.max = noQuestions
            progress.progress = formWithSections.noAnsweredQuestions
            questionsAnswered.text = questionsAnswered.context.getString(
                R.string.no_answered_questions,
                formWithSections.noAnsweredQuestions,
                noQuestions
            )
            progress.visibility =
                if (formWithSections.noAnsweredQuestions == 0) View.INVISIBLE else View.VISIBLE
            questionsAnswered.visibility = View.VISIBLE
            with(item.form) {
                formDescription.apply {
                    text = context.highlight(
                        description,
                        context.getString(R.string.form_suffix, code)
                    )
                }
                formIcon.setImageResource(getIcon(code))
            }
        }

        private fun getIcon(code: String): Int {
            return when {
                code.startsWith(FormCode.A.name) -> R.drawable.ic_form_a
                code.startsWith(FormCode.B.name) -> R.drawable.ic_form_b
                code.startsWith(FormCode.C.name) -> R.drawable.ic_form_default
                code.startsWith(FormCode.D.name) -> R.drawable.ic_form_c
                else -> R.drawable.ic_form_default
            }
        }
    }

    enum class FormCode {
        A, B, C, D
    }
}