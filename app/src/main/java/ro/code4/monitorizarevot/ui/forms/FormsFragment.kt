package ro.code4.monitorizarevot.ui.forms

import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.ui.base.BaseFragment

class FormsFragment : BaseFragment<FormsViewModel>() {
    override val layout: Int
        get() = R.layout.fragment_forms
    override val viewModel: FormsViewModel by viewModel()

}