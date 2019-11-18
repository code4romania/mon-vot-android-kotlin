package ro.code4.monitorizarevot.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_settings.*
import ro.code4.monitorizarevot.BuildConfig
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.browse

class SettingsFragment : Fragment() {

    companion object {
        val TAG = SettingsFragment::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_contact.setOnClickListener { onContactClicked() }

        tv_policy.setOnClickListener { context?.browse(BuildConfig.PRIVACY_WEB_URL) }
    }

    private fun onContactClicked() {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO,
            Uri.fromParts("mailto", BuildConfig.SUPPORT_EMAIL, null)
        )
        startActivity(
            Intent.createChooser(
                emailIntent,
                getString(R.string.settings_send_mail_via)
            )
        )
    }

}