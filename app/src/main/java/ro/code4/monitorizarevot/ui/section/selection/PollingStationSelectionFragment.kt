package ro.code4.monitorizarevot.ui.section.selection

import android.content.Context
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
import ro.code4.monitorizarevot.widget.ProgressDialogFragment


class PollingStationSelectionFragment : ViewModelFragment<PollingStationSelectionViewModel>() {

    private val progressDialog: ProgressDialogFragment by lazy {
        ProgressDialogFragment().also {
            it.isCancelable = false
        }
    }

    companion object {
        val TAG = PollingStationSelectionFragment::class.java.simpleName
    }

    override val layout: Int
        get() = R.layout.fragment_polling_station_selection
    override val screenName: Int
        get() = R.string.analytics_title_station_selection

    override val viewModel: PollingStationSelectionViewModel by viewModel()

    lateinit var parentViewModel: PollingStationViewModel

    private lateinit var countySpinnerAdapter: ArrayAdapter<String>
    private var countyCode: String? = null
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
        countyCode = arguments?.getString(PollingStationActivity.EXTRA_COUNTY_NAME)
        pollingStationId =
            arguments?.getInt(PollingStationActivity.EXTRA_POLLING_STATION_ID, -1) ?: -1
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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

        viewModel.selection().observe(viewLifecycleOwner, Observer {
            countySpinner.setSelection(it.first)
            pollingStationNumber.setText(it.second.toString())
            pollingStationNumber.setSelection(it.second.toString().length)
            pollingStationNumber.isEnabled = true

            if (countyCode != null && pollingStationId > 0) {
                val counties = countiesSource.value?.let { countiesResult ->
                    if (countiesResult is Result.Success) {
                        countiesResult.data
                    } else null
                }
                val targetIndex =
                    counties?.indexOfFirst { countyName -> countyName == countyCode } ?: -1
                if (targetIndex >= 0) {
                    countySpinner.setSelection(targetIndex)
                    pollingStationNumber.setText(pollingStationId.toString())
                }
            }
        })
    }

    override fun onDestroyView() {
        if (progressDialog.isResumed) progressDialog.dismissAllowingStateLoss()
        super.onDestroyView()
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
                parentViewModel.selectCounty(county)
                pollingStationNumber.isEnabled = county != null
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        setContinueButton()
        viewModel.getCounties()
    }

    private fun setCountiesDropdown(counties: List<String>) {
        countySpinnerAdapter.clear()
        countySpinnerAdapter.addAll(counties)
        countySpinnerAdapter.notifyDataSetChanged()
    }


    private fun setContinueButton() {
        continueButton.setOnClickListener {
            parentViewModel.validPollingStationInput(pollingStationNumber.text)
        }
    }
}