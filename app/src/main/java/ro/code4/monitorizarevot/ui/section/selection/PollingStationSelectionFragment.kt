package ro.code4.monitorizarevot.ui.section.selection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_polling_station_selection.*
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.Result
import ro.code4.monitorizarevot.ui.base.ViewModelFragment
import ro.code4.monitorizarevot.ui.section.PollingStationActivity
import ro.code4.monitorizarevot.ui.section.PollingStationViewModel
import ro.code4.monitorizarevot.ui.section.VisitedPollingStationsActivity
import ro.code4.monitorizarevot.widget.ProgressDialogFragment


class PollingStationSelectionFragment : ViewModelFragment<PollingStationSelectionViewModel>() {

    private val progressDialog: ProgressDialogFragment by lazy {
        ProgressDialogFragment().also {
            it.isCancelable = false
        }
    }

    override val layout: Int
        get() = R.layout.fragment_polling_station_selection
    override val screenName: Int
        get() = R.string.analytics_title_station_selection

    override val viewModel: PollingStationSelectionViewModel by viewModel()

    lateinit var parentViewModel: PollingStationViewModel
    private lateinit var countySpinnerAdapter: ArrayAdapter<String>
    private lateinit var municipalitySpinnerAdapter: ArrayAdapter<String>
    private var countyCode: String? = null
    private var municipalityCode: String? = null
    private var _pollingStationNumber = -1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentViewModel = getSharedViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        countySpinnerAdapter = ArrayAdapter(requireActivity(), R.layout.item_spinner, mutableListOf())
        countySpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)

        municipalitySpinnerAdapter = ArrayAdapter(requireActivity(), R.layout.item_spinner, mutableListOf())
        municipalitySpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)

        countyCode = arguments?.getString(PollingStationActivity.EXTRA_COUNTY_NAME)
        municipalityCode = arguments?.getString(PollingStationActivity.EXTRA_MUNICIPALITY_NAME)

        _pollingStationNumber = arguments?.getInt(PollingStationActivity.EXTRA_POLLING_STATION_NUMBER, -1) ?: -1
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val locationsSource = viewModel.locations()
        locationsSource.observe(viewLifecycleOwner, Observer {
            it.handle(
                onSuccess = { locations ->
                    progressDialog.dismiss()
                    locations?.first?.run(::setCountiesDropdown)
                    locations?.second?.run(::setMunicipalitiesDropdown)
                },
                onFailure = {
                    progressDialog.dismiss()
                    // TODO: Show some message for the user know what happened
                },
                onLoading = {
                    activity?.run {
                        progressDialog.showNow(supportFragmentManager, ProgressDialogFragment.TAG)
                    }
                }
            )
        })

        viewModel.selection().observe(viewLifecycleOwner, Observer {
            countySpinner.setSelection(it.first)
            municipalitySpinner.setSelection(it.second)
            pollingStationNumber.setText(it.third.toString())
            pollingStationNumber.setSelection(it.third.toString().length)
            pollingStationNumber.isEnabled = true

            if (countyCode != null && municipalityCode != null && _pollingStationNumber > 0) {
                val counties = locationsSource.value?.let { locationResult ->
                    if (locationResult is Result.Success) {
                        locationResult.data?.first
                    } else null
                }

                val municipalities = locationsSource.value?.let { locationResult ->
                    if (locationResult is Result.Success) {
                        locationResult.data?.second
                    } else null
                }
                val countyTargetIndex = counties?.indexOfFirst { countyName -> countyName == countyCode } ?: -1
                val municipalityTargetIndex = municipalities?.indexOfFirst { municipalityName -> municipalityName == municipalityCode } ?: -1

                if (countyTargetIndex >= 0) {
                    updateCountyDisplay(countyTargetIndex + 1)
                }
                if (municipalityTargetIndex >= 0) {
                    updateMunicipalityDisplay(countyTargetIndex + 1)
                }
            }
        })
    }

    override fun onDestroyView() {
        if (progressDialog.isResumed) progressDialog.dismissAllowingStateLoss()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        kotlin.runCatching {
            visitedStationsButton.visibility = if (viewModel.hasSelectedStation()) View.VISIBLE else View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentViewModel.setTitle(getString(R.string.title_polling_station_selection))
        countySpinner.adapter = countySpinnerAdapter
        countySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val county = viewModel.getSelectedCounty(position)
                val municipalities = viewModel.getMunicipalitiesForCounty(position)
                setMunicipalitiesDropdown(municipalities)

                parentViewModel.selectCounty(county)
                parentViewModel.selectMunicipality(null)
                municipalitySpinner.setSelection(0)
                pollingStationNumber.isEnabled = false
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        municipalitySpinner.adapter = municipalitySpinnerAdapter
        municipalitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val municipality = viewModel.getSelectedMunicipality(parentViewModel.getSelectCounty()?.code, position)
                parentViewModel.selectMunicipality(municipality)
                pollingStationNumber.isEnabled = municipality != null
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        setupActionButtons()
        viewModel.getData()
    }

    private fun setCountiesDropdown(counties: List<String>) {
        countySpinnerAdapter.clear()
        countySpinnerAdapter.addAll(counties)
        countySpinnerAdapter.notifyDataSetChanged()
    }

    private fun setMunicipalitiesDropdown(municipalities: List<String>) {
        municipalitySpinnerAdapter.clear()
        municipalitySpinnerAdapter.addAll(municipalities)
        municipalitySpinnerAdapter.notifyDataSetChanged()
    }

    private fun setupActionButtons() {
        continueButton.setOnClickListener {
            parentViewModel.validPollingStationInput(pollingStationNumber.text)
        }
        visitedStationsButton.setOnClickListener {
            val intent = Intent(requireContext(), VisitedPollingStationsActivity::class.java)
            startActivityForResult(intent, CHOOSE_SECTION_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CHOOSE_SECTION_CODE) {
            val newCountyCode = data?.getStringExtra(PollingStationActivity.EXTRA_COUNTY_NAME)
            val newMunicipalityCode = data?.getStringExtra(PollingStationActivity.EXTRA_MUNICIPALITY_NAME)
            val newPollingStationNr = data?.getIntExtra(PollingStationActivity.EXTRA_POLLING_STATION_NUMBER, -1) ?: -1
          
            if (newMunicipalityCode != null &&  newCountyCode != null && newPollingStationNr > 0 ) {
                countyCode = newCountyCode
                municipalityCode = newMunicipalityCode
                _pollingStationNumber = newPollingStationNr

                for (i in 0 until countySpinnerAdapter.count) {
                    if (countySpinnerAdapter.getItem(i) == newCountyCode) {
                        updateCountyDisplay(i)
                    }
                }

                for (i in 0 until municipalitySpinnerAdapter.count) {
                    if (municipalitySpinnerAdapter.getItem(i) == municipalityCode) {
                        updateMunicipalityDisplay(i)
                    }
                }
            }
        }
    }

    private fun updateCountyDisplay(position: Int) {
        countySpinner.setSelection(position)
        pollingStationNumber.setText(pollingStationNumber.toString())
    }
    private fun updateMunicipalityDisplay(position: Int) {
        municipalitySpinner.setSelection(position)
        pollingStationNumber.setText(pollingStationNumber.toString())
    }

    companion object {
        val TAG = PollingStationSelectionFragment::class.java.simpleName
        const val CHOOSE_SECTION_CODE = 1000
    }
}