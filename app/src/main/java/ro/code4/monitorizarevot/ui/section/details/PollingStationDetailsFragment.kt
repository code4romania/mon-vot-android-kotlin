package ro.code4.monitorizarevot.ui.section.details

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_polling_station_details.*
import kotlinx.android.synthetic.main.fragment_polling_station_selection.continueButton
import kotlinx.android.synthetic.main.widget_change_polling_station_bar.*
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.ui.base.ViewModelFragment
import ro.code4.monitorizarevot.ui.section.PollingStationViewModel
import java.util.*


class PollingStationDetailsFragment : ViewModelFragment<PollingStationViewModel>() {

    companion object {
        val TAG = PollingStationDetailsFragment::class.java.simpleName
    }

    override val layout: Int
        get() = R.layout.fragment_polling_station_details
    override val screenName: Int
        get() = R.string.analytics_title_station_details

    override lateinit var viewModel: PollingStationViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = getSharedViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.pollingStation().observe(viewLifecycleOwner, Observer {
            pollingStationBarText.text = getString(
                R.string.polling_station, it.first, it.second
            )
        })
        viewModel.setTitle(getString(R.string.title_polling_station))
        viewModel.getPollingStationBarText()
        pollingStationBarButton.setOnClickListener {
            viewModel.notifyChangeRequested()
            activity?.onBackPressed()
        }
        viewModel.departureTime().observe(viewLifecycleOwner, Observer {
            departureTime.text = it
        })
        viewModel.arrivalTime().observe(viewLifecycleOwner, Observer {
            arrivalTime.text = it
        })
        arrivalTime.setOnClickListener {
            showDatePicker(
                R.string.polling_station_choose_date_enter,
                R.string.polling_station_choose_time_enter,
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
                R.string.polling_station_choose_date_leave,
                R.string.polling_station_choose_time_leave,
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
        viewModel.selectedPollingStation().observe(viewLifecycleOwner, Observer {
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
            requireActivity(),
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