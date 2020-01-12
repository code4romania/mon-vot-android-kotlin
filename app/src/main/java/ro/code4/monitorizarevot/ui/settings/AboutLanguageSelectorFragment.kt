package ro.code4.monitorizarevot.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatSpinner
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.core.KoinComponent
import org.koin.core.inject
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.LanguageAdapter
import ro.code4.monitorizarevot.adapters.OnboardingAdapter
import ro.code4.monitorizarevot.helper.getLocale
import ro.code4.monitorizarevot.helper.getLocaleCode
import ro.code4.monitorizarevot.helper.setLocaleCode
import ro.code4.monitorizarevot.ui.onboarding.encode
import ro.code4.monitorizarevot.ui.onboarding.getLocales
import java.util.*

class AboutLanguageSelectorFragment : BottomSheetDialogFragment(), KoinComponent,
    OnboardingAdapter.OnLanguageChangedListener {

    companion object {
        val TAG = AboutLanguageSelectorFragment::class.java.simpleName
    }

    private val preferences: SharedPreferences by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about_language_selector, container, false)
        val languages = getLocales(requireActivity().resources.getStringArray(R.array.languages))
        val languagesAdapter = LanguageAdapter(requireContext(), languages)
        val selectedLocale = preferences.getLocaleCode().getLocale()
        view.findViewById<Spinner>(R.id.languagesSpinner).apply {
            adapter = languagesAdapter
            setSelection(languages.indexOf(selectedLocale))
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                // when it is initialized, the Spinner automatically invokes this listener,
                // we use this flag to avoid this, otherwise the dialog wouldn't be shown(this initial
                // selection would trigger the else branch and dismiss() the dialog!)
                var firstTime = true

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    if (firstTime) {
                        firstTime = false
                        return
                    }
                    val locale = languagesAdapter.getItem(position)
                    if (locale != null && locale != selectedLocale) {
                        onLanguageChanged(locale)
                    } else {
                        dismiss()
                    }
                }
            }
        }
        return view
    }

    override fun onLanguageChanged(locale: Locale) {
        dismiss()
        preferences.setLocaleCode(locale.encode())
        requireActivity().recreate()
    }
}

/**
 * By default, a [Spinner] widget only triggers its [AdapterView.OnItemSelectedListener] only if the selected
 * element is different than the current selection. Receiving the selection of the same entry is needed so
 * we can cancel the language selection dialog, event when the user selects the same language.
 */
class SameSelectionAllowedSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatSpinner(context, attrs) {

    private var lastSelectedPosition = -1

    override fun setSelection(position: Int) {
        super.setSelection(position)
        triggerListenerIfPossible(position)
    }

    override fun setSelection(position: Int, animate: Boolean) {
        super.setSelection(position, animate)
        triggerListenerIfPossible(position)
    }

    private fun triggerListenerIfPossible(newSelection: Int) {
        if (lastSelectedPosition == newSelection) {
            onItemSelectedListener?.onItemSelected(this, null, newSelection, -1)
        }
        lastSelectedPosition = newSelection
    }
}
