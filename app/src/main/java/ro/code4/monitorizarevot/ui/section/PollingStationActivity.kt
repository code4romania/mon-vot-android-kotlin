package ro.code4.monitorizarevot.ui.section

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_polling_station.*
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.hideKeyboard
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

    private var countyName: String? = null
    private var pollingStationId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        countyName = intent.getStringExtra(EXTRA_COUNTY_NAME)
        pollingStationId = intent.getIntExtra(EXTRA_POLLING_STATION_ID, -1)

        viewModel.title().observe(this, Observer {
            title = it
        })
        viewModel.nextToMain().observe(this, Observer {
            viewModel.registerStationSelection()
            startActivityWithoutTrace(MainActivity::class.java)
        })
        viewModel.next().observe(this, Observer {
            hideKeyboard()
            replaceFragment(
                R.id.container,
                PollingStationDetailsFragment(),
                tag = PollingStationDetailsFragment.TAG
            )
        })
        replaceFragment(R.id.container, PollingStationSelectionFragment().apply {
            if (countyName != null && pollingStationId > 0) {
                arguments = bundleOf(
                    EXTRA_COUNTY_NAME to countyName,
                    EXTRA_POLLING_STATION_ID to pollingStationId
                )
            }
        })
    }

    companion object {
        const val EXTRA_COUNTY_NAME = "extra_county_name"
        const val EXTRA_POLLING_STATION_ID = "extra_polling_station_id"
    }

}