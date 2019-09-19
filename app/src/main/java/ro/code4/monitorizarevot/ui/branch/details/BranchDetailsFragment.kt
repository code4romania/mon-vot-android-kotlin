package ro.code4.monitorizarevot.ui.branch.details

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_branch_details.*
import kotlinx.android.synthetic.main.fragment_branch_selection.continueButton
import kotlinx.android.synthetic.main.widget_change_branch_bar.*
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.ui.base.BaseFragment
import ro.code4.monitorizarevot.ui.branch.BranchViewModel
import java.util.*


class BranchDetailsFragment : BaseFragment<BranchViewModel>() {

    companion object {
        val TAG = BranchDetailsFragment::class.java.simpleName
    }

    override val layout: Int
        get() = R.layout.fragment_branch_details
    override lateinit var viewModel: BranchViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = getSharedViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.branchBarText().observe(this, Observer {
            branchBarText.text = it
        })
        viewModel.setTitle(getString(R.string.title_branch_details))
        viewModel.getBranchBarText()
        branchBarButton.setOnClickListener {
            activity?.onBackPressed() //TODO fix crash on back
        }
        arrivalTime.setOnClickListener {
            showTimePicker(R.string.branch_choose_time_enter,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    viewModel.setArrivalTime(hourOfDay, minute)
                    arrivalTime.text = viewModel.getArrivalTime()
                })
        }
        departureTime.setOnClickListener {
            showTimePicker(R.string.branch_choose_time_leave,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    viewModel.setDepartureTime(hourOfDay, minute)
                    departureTime.text = viewModel.getDepartureTime()
                })
        }
        setContinueButton()
    }

    private fun showTimePicker(titleId: Int, listener: TimePickerDialog.OnTimeSetListener) {
        val now = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            activity,
            listener, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true
        )
        timePickerDialog.setTitle(titleId)
        timePickerDialog.show()
    }

    private fun setContinueButton() {
        continueButton.setOnClickListener {
            viewModel.validateInputDetails(
                environmentRadioGroup.checkedRadioButtonId,
                sexRadioGroup.checkedRadioButtonId
            )

        }
    }
}