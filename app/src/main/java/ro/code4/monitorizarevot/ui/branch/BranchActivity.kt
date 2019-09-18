package ro.code4.monitorizarevot.ui.branch

import android.os.Bundle
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_branch.*
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.replaceFragment
import ro.code4.monitorizarevot.ui.base.BaseActivity
import ro.code4.monitorizarevot.ui.branch.details.BranchDetailsFragment
import ro.code4.monitorizarevot.ui.branch.selection.BranchSelectionFragment

class BranchActivity : BaseActivity<BranchViewModel>() {
    override val layout: Int
        get() = R.layout.activity_branch
    override val viewModel: BranchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        viewModel.title().observe(this, Observer {
            title = it
        })
        viewModel.next().observe(this, Observer {
            //            Log.i("GAGA", "YAY")
            replaceFragment(R.id.container, BranchDetailsFragment())
        })
        replaceFragment(R.id.container, BranchSelectionFragment())
    }
}