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
    private var pollingStationId = -1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentViewModel = getSharedViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        countySpinnerAdapter =
            ArrayAdapter(requireActivity(), R.layout.item_spinner, mutableListOf())
        countySpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)

        municipalitySpinnerAdapter =
            ArrayAdapter(requireActivity(), R.layout.item_spinner, mutableListOf())
        municipalitySpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)

        countyCode = arguments?.getString(PollingStationActivity.EXTRA_COUNTY_NAME)
        pollingStationId =
            arguments?.getInt(PollingStationActivity.EXTRA_POLLING_STATION_ID, -1) ?: -1
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val countiesSource = viewModel.counties()
        val municipalitySource = viewModel.municipalities()

        countiesSource.observe(viewLifecycleOwner, Observer {
            it.handle(
                onSuccess = { counties ->
                    progressDialog.dismiss()
                    counties?.run(::setCountiesDropdown)
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

        municipalitySource.observe(viewLifecycleOwner, Observer {
            it.handle(
                onSuccess = { municipalities ->
                    progressDialog.dismiss()
                    municipalities?.run(::setMunicipalitiesDropdown)
                },
                onFailure = {
                    progressDialog.dismiss()
                    // TODO: Show some message for the user know what happened
                },
                onLoading = {
                    activity?.run {
                        if(!progressDialog.isAdded){
                            progressDialog.showNow(supportFragmentManager, ProgressDialogFragment.TAG)
                        }
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

            if (municipalityCode != null && countyCode != null && pollingStationId > 0) {
                val counties = countiesSource.value?.let { countiesResult ->
                    if (countiesResult is Result.Success) {
                        countiesResult.data
                    } else null
                }
                val countyIndex = counties?.indexOfFirst { code -> code == countyCode } ?: -1
                if (countyIndex >= 0) {
                    updateCountySelectionDisplay(countyIndex)
                }

                val municipalities = municipalitySource.value?.let { municipalitiesResult ->
                    if (municipalitiesResult is Result.Success) {
                        municipalitiesResult.data
                    } else null
                }
                val targetIndex = municipalities?.indexOfFirst { code -> code == municipalityCode } ?: -1
                if (targetIndex >= 0) {
                    updateMunicipalitySelectionDisplay(targetIndex)
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
            visitedStationsButton.visibility =
                if (viewModel.hasSelectedStation()) View.VISIBLE else View.GONE
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
                val county = viewModel.selectedCountyAt(position)

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
                val newMunicipality = viewModel.selectMunicipalityAt(position)
                parentViewModel.selectMunicipality(newMunicipality)
                pollingStationNumber.isEnabled = newMunicipality != null
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        setupActionButtons()
        viewModel.getCounties()
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
            parentViewModel.validPollingStationNumber(pollingStationNumber.text)
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
            val newPollingStationNr = data?.getIntExtra(PollingStationActivity.EXTRA_POLLING_STATION_ID, -1) ?: -1
            if (newCountyCode != null && newMunicipalityCode != null && newPollingStationNr > 0) {
                countyCode = newCountyCode
                municipalityCode = newMunicipalityCode
                pollingStationId = newPollingStationNr

                for (i in 0 until countySpinnerAdapter.count) {
                    if (countySpinnerAdapter.getItem(i) == newCountyCode) {
                        updateCountySelectionDisplay(i)
                    }
                }


                for (i in 0 until municipalitySpinnerAdapter.count) {
                    if (municipalitySpinnerAdapter.getItem(i) == newMunicipalityCode) {
                        updateMunicipalitySelectionDisplay(i)
                    }
                }
            }
        }
    }

    private fun updateCountySelectionDisplay(position: Int) {
        countySpinner.setSelection(position)
        pollingStationNumber.setText(pollingStationId.toString())
    }

    private fun updateMunicipalitySelectionDisplay(position: Int) {
        countySpinner.setSelection(position)
        pollingStationNumber.setText(pollingStationId.toString())
    }

    companion object {
        val TAG = PollingStationSelectionFragment::class.java.simpleName
        const val CHOOSE_SECTION_CODE = 1000
    }
}