package ro.code4.monitorizarevot.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import ro.code4.monitorizarevot.R

/**
 * .:.:.:. Created by @henrikhorbovyi on 13/10/19 .:.:.:.
 */
class ProgressDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_progress_dialog, container, false)
    }

    companion object {
        const val tag = "ProgressDialogFragment"
    }

}
