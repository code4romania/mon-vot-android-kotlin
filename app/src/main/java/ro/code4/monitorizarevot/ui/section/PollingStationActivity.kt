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

    private var provinceName: String? = null
    private var countyName: String? = null
    private var municipalityName: String? = null
    private var pollingStationNumber = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        provinceName = intent.getStringExtra(EXTRA_PROVINCE_NAME)
        countyName = intent.getStringExtra(EXTRA_COUNTY_NAME)
        municipalityName = intent.getStringExtra(EXTRA_MUNICIPALITY_NAME)
        pollingStationNumber = intent.getIntExtra(EXTRA_POLLING_STATION_NUMBER, -1)

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
            if (provinceName != null && countyName != null && municipalityName != null && pollingStationNumber > 0) {
                arguments = bundleOf(
                    EXTRA_PROVINCE_NAME to provinceName,
                    EXTRA_COUNTY_NAME to countyName,
                    EXTRA_MUNICIPALITY_NAME to municipalityName,
                    EXTRA_POLLING_STATION_NUMBER to pollingStationNumber
                )
            }
        })
    }

    companion object {
        const val EXTRA_PROVINCE_NAME = "extra_province_name"
        const val EXTRA_COUNTY_NAME = "extra_county_name"
        const val EXTRA_MUNICIPALITY_NAME = "extra_municipality_name"
        const val EXTRA_POLLING_STATION_NUMBER = "extra_polling_station_number"
    }

}