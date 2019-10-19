package ro.code4.monitorizarevot.ui.branch.selection

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_branch_selection.*
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.CountyAdapter
import ro.code4.monitorizarevot.data.model.County
import ro.code4.monitorizarevot.ui.base.BaseFragment
import ro.code4.monitorizarevot.ui.branch.BranchViewModel


class BranchSelectionFragment : BaseFragment<BranchSelectionViewModel>() {

    companion object {
        val TAG = BranchSelectionFragment::class.java.simpleName
    }

    override val layout: Int
        get() = R.layout.fragment_branch_selection
    override val viewModel: BranchSelectionViewModel by viewModel()

    lateinit var parentViewModel: BranchViewModel

//    lateinit var countySpinnerAdapter: CountyAdapter
    lateinit var countySpinnerAdapter: ArrayAdapter<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentViewModel = getSharedViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        countySpinnerAdapter =
            ArrayAdapter(activity!!, R.layout.support_simple_spinner_dropdown_item, mutableListOf())
        countySpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.counties().observe(viewLifecycleOwner, Observer {
            setCountiesDropdown(it)
        })

        viewModel.selection().observe(viewLifecycleOwner, Observer {
            countySpinner.setSelection(it.first)
            branchNumber.setText(it.second.toString())
            branchNumber.setSelection(it.second.toString().length)
            branchNumber.isEnabled = true
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentViewModel.setTitle(getString(R.string.title_branch_selection))
        countySpinner.adapter = countySpinnerAdapter
        countySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val county = viewModel.getSelectedCounty(position)
                county?.let {
                    parentViewModel.selectCounty(it)
                }
                branchNumber.isEnabled = county != null
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
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
            parentViewModel.validBranchInput(branchNumber.text)
        }
    }
}