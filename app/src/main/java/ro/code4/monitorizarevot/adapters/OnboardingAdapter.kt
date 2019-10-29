package ro.code4.monitorizarevot.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_onboarding_choose_language.view.*
import kotlinx.android.synthetic.main.item_onboarding_tutorial.view.*
import kotlinx.android.synthetic.main.item_onboarding_tutorial.view.icOnboarding
import kotlinx.android.synthetic.main.item_onboarding_tutorial.view.titleOnboarding
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.helper.OnboardingChooseLanguageScreen
import ro.code4.monitorizarevot.adapters.helper.OnboardingScreen
import ro.code4.monitorizarevot.adapters.helper.OnboardingTutorialScreen
import ro.code4.monitorizarevot.adapters.helper.ViewHolder
import ro.code4.monitorizarevot.helper.toHtml
import java.util.*


class OnboardingAdapter(
    private val context: Context,
    private val screens: ArrayList<OnboardingScreen>,
    private val selectedLocale: Locale,
    private val languageChangedListener: OnLanguageChangedListener
) : RecyclerView.Adapter<ViewHolder>() {
    companion object {
        const val ONBOARDING_TUTORIAL = 0
        const val ONBOARDING_LANGUAGE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewResId = when (viewType) {
            ONBOARDING_TUTORIAL -> R.layout.item_onboarding_tutorial
            else -> R.layout.item_onboarding_choose_language
        }
        return ViewHolder(LayoutInflater.from(context).inflate(viewResId, parent, false))
    }

    override fun getItemCount(): Int = screens.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        val screen = screens[position]
        when (viewType) {
            ONBOARDING_TUTORIAL -> {
                holder.itemView.descriptionOnboarding.text =
                    context.getString((screen as OnboardingTutorialScreen).descriptionResId)
                        .toHtml()
            }
            else -> {
                val languages = (screen as OnboardingChooseLanguageScreen).languages
                val adapter = LanguageAdapter(context, languages)
                with(holder.itemView.languagesSpinner) {
                    this.adapter = adapter
                    setSelection(languages.indexOf(selectedLocale))
                    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(p0: AdapterView<*>?) = Unit

                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val locale = adapter.getItem(position)
                            if (locale != null && locale != selectedLocale) {
                                languageChangedListener.onLanguageChanged(locale)
                            }

                        }

                    }
                }

            }
        }
        with(screen) {
            holder.itemView.titleOnboarding.text = context.getString(titleResId)
            holder.itemView.icOnboarding.setImageResource(imageResId)
        }
    }

    override fun getItemViewType(position: Int): Int = when (screens[position]) {
        is OnboardingTutorialScreen -> ONBOARDING_TUTORIAL
        else -> ONBOARDING_LANGUAGE
    }

    interface OnLanguageChangedListener {
        fun onLanguageChanged(locale: Locale)
    }
}