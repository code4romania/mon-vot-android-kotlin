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
    private lateinit var provinceSpinnerAdapter: ArrayAdapter<String>
    private lateinit var countySpinnerAdapter: ArrayAdapter<String>
    private lateinit var municipalitySpinnerAdapter: ArrayAdapter<String>
    private var provinceCode: String? = null
    private var countyCode: String? = null
    private var municipalityCode: String? = null
    private var pollingStationNumber = -1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentViewModel = getSharedViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        provinceSpinnerAdapter = ArrayAdapter(requireActivity(), R.layout.item_spinner, mutableListOf())
        provinceSpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        provinceCode = arguments?.getString(PollingStationActivity.EXTRA_PROVINCE_NAME)

        countySpinnerAdapter = ArrayAdapter(requireActivity(), R.layout.item_spinner, mutableListOf())
        countySpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        countyCode = arguments?.getString(PollingStationActivity.EXTRA_COUNTY_NAME)

        municipalitySpinnerAdapter = ArrayAdapter(requireActivity(), R.layout.item_spinner, mutableListOf())
        municipalitySpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        municipalityCode = arguments?.getString(PollingStationActivity.EXTRA_MUNICIPALITY_NAME)

        pollingStationNumber =
            arguments?.getInt(PollingStationActivity.EXTRA_POLLING_STATION_NUMBER, -1) ?: -1
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val provincesSource = viewModel.provinces()
        provincesSource.observe(viewLifecycleOwner, Observer {
            it.handle(
                onSuccess = { provinces ->
                    progressDialog.dismiss()
                    provinces?.run(::setProvincesDropdown)
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

        val countiesSource = viewModel.counties()
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

        val municipalitiesSource = viewModel.municipalities()
        municipalitiesSource.observe(viewLifecycleOwner, Observer {
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
                       progressDialog.showNow(supportFragmentManager, ProgressDialogFragment.TAG)
                    }
                }
            )
        })

        viewModel.selection().observe(viewLifecycleOwner, Observer {
            provinceSpinner.setSelection(it.first)
            countySpinner.setSelection(it.second)
            municipalitySpinner.setSelection(it.third)
            pollingStationNumberInput.setText(it.forth.toString())
            pollingStationNumberInput.setSelection(it.forth.toString().length)
            municipalitySpinner.isEnabled = true
            pollingStationNumberInput.isEnabled = true

            if (provinceCode != null && countyCode != null && municipalityCode != null && pollingStationNumber > 0) {
                val provinces = provincesSource.value?.let { provincesResult ->
                    if (provincesResult is Result.Success) {
                        provincesResult.data
                    } else null
                }
                val targetProvinceIndex = provinces?.indexOfFirst { provinceName -> provinceName == provinceCode } ?: -1

                val counties = countiesSource.value?.let { countiesResult ->
                    if (countiesResult is Result.Success) {
                        countiesResult.data
                    } else null
                }
                val targetCountyIndex = counties?.indexOfFirst { countyName -> countyName == countyCode } ?: -1

                val municipalities = municipalitiesSource.value?.let { municipalitiesResult ->
                    if (municipalitiesResult is Result.Success) {
                        municipalitiesResult.data
                    } else null
                }
                val targetMunicipalitiesIndex =
                    municipalities?.indexOfFirst { municipality -> municipality == municipalityCode } ?: -1

                if (targetProvinceIndex >= 0 && targetCountyIndex >= 0 && targetMunicipalitiesIndex > 0) {
                    updateSelectionDisplay(targetProvinceIndex, targetCountyIndex, targetMunicipalitiesIndex)
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

        countySpinner.isEnabled = false
        municipalitySpinner.isEnabled = false

        parentViewModel.setTitle(getString(R.string.title_polling_station_selection))
        provinceSpinner.adapter = provinceSpinnerAdapter
        provinceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val province = viewModel.getSelectedProvince(position)
                parentViewModel.selectProvince(province)
                parentViewModel.deselectCounty()
                parentViewModel.deselectMunicipality()
                countySpinner.setSelection(0)
                municipalitySpinner.setSelection(0)

                if (province != null) {
                    countySpinner.isEnabled = true
                    viewModel.getCounties(province.code)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        countySpinner.adapter = countySpinnerAdapter
        countySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val province = viewModel.getSelectedProvince(provinceSpinner.selectedItemPosition)
                val county = viewModel.getSelectedCounty(position)
                parentViewModel.selectCounty(county)
                parentViewModel.deselectMunicipality()
                if (county != null && province != null) {
                    municipalitySpinner.isEnabled = true
                    viewModel.getMunicipalities(province.code, county.code)
                    municipalitySpinner.setSelection(0)
                }

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
                val municipality = viewModel.getSelectedMunicipality(position)
                if (municipality != null) {
                    parentViewModel.selectMunicipality(municipality)
                    pollingStationNumberInput.isEnabled = true
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        setupActionButtons()
        viewModel.getProvinces()
    }

    private fun setProvincesDropdown(provinces: List<String>) {
        provinceSpinnerAdapter.clear()
        provinceSpinnerAdapter.addAll(provinces)
        provinceSpinnerAdapter.notifyDataSetChanged()
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
            parentViewModel.validPollingStationInput(pollingStationNumberInput.text)
        }
        visitedStationsButton.setOnClickListener {
            val intent = Intent(requireContext(), VisitedPollingStationsActivity::class.java)
            startActivityForResult(intent, CHOOSE_SECTION_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CHOOSE_SECTION_CODE) {
            val newProvinceCode = data?.getStringExtra(PollingStationActivity.EXTRA_PROVINCE_NAME)
            val newCountyCode = data?.getStringExtra(PollingStationActivity.EXTRA_COUNTY_NAME)
            val newMunicipalityCode = data?.getStringExtra(PollingStationActivity.EXTRA_MUNICIPALITY_NAME)
            val newPollingStationNr = data?.getIntExtra(PollingStationActivity.EXTRA_POLLING_STATION_NUMBER, -1) ?: -1
            if (newProvinceCode != null && newCountyCode != null && newMunicipalityCode != null && newPollingStationNr > 0) {
                countyCode = newCountyCode
                municipalityCode = newMunicipalityCode
                pollingStationNumber = newPollingStationNr

                var provincePosition = -1
                var countyPosition = -1
                var municipalityPosition = -1

                for (i in 0 until provinceSpinnerAdapter.count) {
                    if (provinceSpinnerAdapter.getItem(i) == newProvinceCode) {
                        provincePosition = i
                    }
                }

                for (i in 0 until countySpinnerAdapter.count) {
                    if (countySpinnerAdapter.getItem(i) == newCountyCode) {
                        countyPosition = i
                    }
                }
                for (i in 0 until municipalitySpinnerAdapter.count) {
                    if (municipalitySpinnerAdapter.getItem(i) == newMunicipalityCode) {
                        municipalityPosition = i
                    }
                }

                updateSelectionDisplay(provincePosition, countyPosition, municipalityPosition)
            }
        }
    }

    private fun updateSelectionDisplay(provincePosition: Int, countyPosition: Int, municipalityPosition: Int) {
        provinceSpinner.setSelection(provincePosition)
        countySpinner.setSelection(countyPosition)
        municipalitySpinner.setSelection(municipalityPosition)
        pollingStationNumberInput.setText(pollingStationNumber.toString())
    }

    companion object {
        val TAG = PollingStationSelectionFragment::class.java.simpleName
        const val CHOOSE_SECTION_CODE = 1000
    }
}