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
    lateinit var countySpinnerAdapter: ArrayAdapter<County>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentViewModel = getSharedViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.counties().observe(this, Observer {
            setCountiesDropdown(it)
        })
        viewModel.getCounties()
        parentViewModel.setTitle(getString(R.string.title_branch_selection))

        setContinueButton()
    }

    private fun setCountiesDropdown(counties: List<County>) {
//        countySpinnerAdapter = CountyAdapter(activity!!, R.layout.support_simple_spinner_dropdown_item, counties)
        countySpinnerAdapter =
            ArrayAdapter(activity!!, R.layout.support_simple_spinner_dropdown_item, counties)
        countySpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        countySpinner.adapter = countySpinnerAdapter
        countySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                parentViewModel.selectCounty(countySpinnerAdapter.getItem(position))
                branchNumber.isEnabled = true
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun setContinueButton() {
        continueButton.setOnClickListener {
            parentViewModel.validBranchInput(branchNumber.text)

        }
    }
}