package ro.code4.monitorizarevot.ui.section

import android.os.Bundle
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_polling_station.*
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.replaceFragment
import ro.code4.monitorizarevot.helper.startActivityWithoutTrace
import ro.code4.monitorizarevot.ui.base.BaseActivity
import ro.code4.monitorizarevot.ui.main.MainActivity
import ro.code4.monitorizarevot.ui.section.details.PollingStationDetailsFragment
import ro.code4.monitorizarevot.ui.section.selection.PollingStationSelectionFragment

class PollingStationActivity : BaseActivity<PollingStationViewModel>() {
    override val layout: Int
        get() = R.layout.activity_polling_station
    override val viewModel: PollingStationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        viewModel.title().observe(this, Observer {
            title = it
        })
        viewModel.nextToMain().observe(this, Observer {
            startActivityWithoutTrace(MainActivity::class.java)
        })
        viewModel.next().observe(this, Observer {
            replaceFragment(
                R.id.container,
                PollingStationDetailsFragment(),
                tag = PollingStationDetailsFragment.TAG
            )
        })
        replaceFragment(R.id.container, PollingStationSelectionFragment())
    }


}