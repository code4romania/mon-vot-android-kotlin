package ro.code4.monitorizarevot.ui.branch.selection

import android.os.Bundle
import android.view.View
import org.koin.android.ext.android.inject
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.adapters.CountyAdapter
import ro.code4.monitorizarevot.ui.base.BaseFragment


class BranchSelectionFragment : BaseFragment<BranchSelectionViewModel>() {
    override val layout: Int
        get() = R.layout.fragment_branch_selection
    override val viewModel: BranchSelectionViewModel by inject()

    lateinit var countySpinnerAdapter: CountyAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCountiesDropdown()
        setContinueButton()
    }

    private fun setCountiesDropdown() {
//        countySpinnerAdapter = CountyAdapter(
//            activity!!,
//           R.layout.support_simple_spinner_dropdown_item,
//            Data.getInstance().getCounties()
//        )
//        dropdown.adapter = countySpinnerAdapter
//        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View,
//                position: Int,
//                id: Long
//            ) {
//                selectedCounty = countySpinnerAdapter.getItem(position)
//                branchNumber.isEnabled = true
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//
//            }
//        }
    }

    private fun setContinueButton() {
//        button.setOnClickListener {
//            if (selectedCounty == null) {
//                Toast.makeText(activity, ro.code4.monitorizarevot.R.string.invalid_branch_county, Toast.LENGTH_SHORT).show()
//            } else if (branchNumber.text.toString().length() === 0) {
//                Toast.makeText(activity, ro.code4.monitorizarevot.R.string.invalid_branch_number, Toast.LENGTH_SHORT).show()
//            } else if (getBranchNumber() <= 0) {
//                Toast.makeText(activity, ro.code4.monitorizarevot.R.string.invalid_branch_number_minus, Toast.LENGTH_SHORT)
//                    .show()
//            } else if (getBranchNumber() > selectedCounty.getBranchesCount()) {
//                Toast.makeText(activity, getBranchExceededError(), Toast.LENGTH_SHORT).show()
//            } else {
//                persistSelection()
//                navigateTo(BranchDetailsFragment.newInstance())
//            }
//        }
    }
}