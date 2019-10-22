package ro.code4.monitorizarevot.ui.branch.details

import android.app.DatePickerDialog
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
        viewModel.branchDetails().observe(this, Observer {
            branchBarText.text = it
        })
        viewModel.setTitle(getString(R.string.title_branch_details))
        viewModel.getBranchBarText()
        branchBarButton.setOnClickListener {
            viewModel.notifyChangeRequested()
            activity?.onBackPressed()
        }
        viewModel.departureTime().observe(this, Observer {
            departureTime.text = it
        })
        viewModel.arrivalTime().observe(this, Observer {
            arrivalTime.text = it
        })
        arrivalTime.setOnClickListener {
            showDatePicker(
                R.string.branch_choose_date_enter,
                R.string.branch_choose_time_enter,
                object : DateTimeListener {
                    override fun onDateTimeChanged(
                        year: Int,
                        month: Int,
                        dayOfMonth: Int,
                        hourOfDay: Int,
                        minute: Int
                    ) {
                        viewModel.setArrivalTime(year, month, dayOfMonth, hourOfDay, minute)
                    }
                })
        }
        departureTime.setOnClickListener {
            showDatePicker(
                R.string.branch_choose_date_leave,
                R.string.branch_choose_time_leave,
                object : DateTimeListener {
                    override fun onDateTimeChanged(
                        year: Int,
                        month: Int,
                        dayOfMonth: Int,
                        hourOfDay: Int,
                        minute: Int
                    ) {
                        viewModel.setDepartureTime(year, month, dayOfMonth, hourOfDay, minute)
                    }
                })
        }
        viewModel.selectedBranch().observe(this, Observer {
            setSelection(it)
        })
        setContinueButton()
    }

    private fun setSelection(pair: Pair<Int?, Int?>) {
        environmentRadioGroup.clearCheck()
        genderRadioGroup.clearCheck()

        pair.first?.let { environmentRadioGroup.check(it) }
        pair.second?.let { genderRadioGroup.check(it) }
    }

    private fun showDatePicker(dateTitleId: Int, timeTitleId: Int, listener: DateTimeListener) {
        val now = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            activity!!,
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                showTimePicker(timeTitleId, year, month, day, listener)
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.setTitle(dateTitleId)
        datePickerDialog.show()
    }

    private fun showTimePicker(
        titleId: Int,
        year: Int,
        month: Int,
        day: Int,
        listener: DateTimeListener
    ) {
        val now = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            activity,
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                listener.onDateTimeChanged(year, month, day, hour, minute)
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true
        )
        timePickerDialog.setTitle(titleId)
        timePickerDialog.show()
    }

    private fun setContinueButton() {
        continueButton.setOnClickListener {
            viewModel.validateInputDetails(
                environmentRadioGroup.checkedRadioButtonId,
                genderRadioGroup.checkedRadioButtonId
            )

        }
    }

    interface DateTimeListener {
        fun onDateTimeChanged(year: Int, month: Int, dayOfMonth: Int, hourOfDay: Int, minute: Int)
    }
}