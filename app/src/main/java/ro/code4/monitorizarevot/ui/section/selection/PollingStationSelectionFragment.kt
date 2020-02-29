package ro.code4.monitorizarevot.ui.section.selection

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_polling_station_selection.*
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.hideKeyboard
import ro.code4.monitorizarevot.ui.base.BaseAnalyticsFragment
import ro.code4.monitorizarevot.ui.base.ViewModelFragment
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentViewModel = getSharedViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        countySpinnerAdapter =
            ArrayAdapter(activity!!, R.layout.item_spinner, mutableListOf())
        countySpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.counties().observe(viewLifecycleOwner, Observer {
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
        })
        setupHideKeyboardListeners(polling_station_select_scrollview)
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

    private fun setupHideKeyboardListeners(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener(View.OnTouchListener { v, event ->
                hideKeyboard()
                false
            })
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupHideKeyboardListeners(innerView)
            }
        }
    }


    private fun setContinueButton() {
        continueButton.setOnClickListener {
            parentViewModel.validPollingStationInput(pollingStationNumber.text)
        }
    }
}