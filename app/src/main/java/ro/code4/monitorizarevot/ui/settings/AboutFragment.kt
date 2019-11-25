package ro.code4.monitorizarevot.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_about.*
import ro.code4.monitorizarevot.BuildConfig
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.analytics.Event
import ro.code4.monitorizarevot.analytics.Param
import ro.code4.monitorizarevot.analytics.ParamKey
import ro.code4.monitorizarevot.helper.browse
import ro.code4.monitorizarevot.helper.logW
import ro.code4.monitorizarevot.helper.toHtml
import ro.code4.monitorizarevot.ui.base.BaseAnalyticsFragment

class AboutFragment : BaseAnalyticsFragment() {
    override val screenName: Int
        get() = R.string.menu_about

    companion object {
        val TAG = AboutFragment::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appVersion.text = context?.getString(R.string.about_app_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)

        content.text = getString(R.string.about_content).toHtml()
        content.movementMethod = LinkMovementMethod.getInstance()

        optionChangeLanguage.setOnClickListener { onChangeLanguageClicked(view) }

        optionContact.setOnClickListener { onContactClicked(view) }

        optionViewPolicy.setOnClickListener { onViewPolicyClicked(view) }
    }

    private fun onChangeLanguageClicked(view: View) {
        logClickEvent(view)
        // TODO: Implement
    }

    private fun onContactClicked(view: View) {
        logClickEvent(view)
        val emailIntent = Intent(
            Intent.ACTION_SENDTO,
            Uri.fromParts("mailto", BuildConfig.SUPPORT_EMAIL, null)
        )
        startActivity(
            Intent.createChooser(
                emailIntent,
                getString(R.string.about_send_mail_via)
            )
        )
    }

    private fun onViewPolicyClicked(view: View) {
        logClickEvent(view)
        val result = context?.browse(BuildConfig.PRIVACY_WEB_URL)
        if (!result!!) {
            logW("No app to view " + BuildConfig.PRIVACY_WEB_URL)
        }
    }

    private fun logClickEvent(view: View) {
        logAnalyticsEvent(
            Event.BUTTON_CLICK,
            Param(ParamKey.NAME, resources.getResourceEntryName(view.id))
        )
    }
}