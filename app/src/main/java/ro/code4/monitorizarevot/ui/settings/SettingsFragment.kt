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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_version.text = context?.getString(R.string.app_version, BuildConfig.VERSION_NAME)

        iv_back.setOnClickListener { requireActivity().finish() }

        fl_language.setOnClickListener {
            //TODO redirect user to language picker screen
        }

        fl_contact.setOnClickListener { onContactClicked() }

        fl_policy.setOnClickListener { context?.browse("https://www.google.com") }
    }

    private fun onContactClicked() {
        val email = "andrei.mares06@gmail.com"
        val emailIntent = Intent(
            Intent.ACTION_SENDTO,
            Uri.fromParts("mailto", email, null)
        )
        startActivity(
            Intent.createChooser(
                emailIntent,
                getString(R.string.settings_send_mail_via)
            )
        )
    }

}