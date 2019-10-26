package ro.code4.monitorizarevot.ui.section

import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.data.model.County
import ro.code4.monitorizarevot.data.model.PollingStation
import ro.code4.monitorizarevot.helper.*
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel
import java.util.*


class PollingStationViewModel : BaseViewModel() {
    private val nextLiveData = SingleLiveEvent<Void>()
    private val nextToMainLiveData = SingleLiveEvent<Void>()
    private val titleLiveData = MutableLiveData<String>()
    private val pollingStationLiveData = MutableLiveData<String>()
    private val arrivalTimeLiveData = MutableLiveData<String>()
    private val departureTimeLiveData = MutableLiveData<String>()
    private val selectedPollingStationLiveData = MutableLiveData<Pair<Int?, Int?>>()

    private val preferences: SharedPreferences by inject()
    private val app: Application by inject()
    private val repository: Repository by inject()
    private lateinit var selectedCounty: County
    private var selectedPollingStationNumber: Int = -1
    private lateinit var arrival: Calendar
    private var departure: Calendar? = null
    fun next(): SingleLiveEvent<Void> = nextLiveData
    fun nextToMain(): SingleLiveEvent<Void> = nextToMainLiveData

    fun title(): LiveData<String> = titleLiveData
    fun setTitle(title: String) = titleLiveData.postValue(title)

    fun arrivalTime(): LiveData<String> = arrivalTimeLiveData
    fun departureTime(): LiveData<String> = departureTimeLiveData
    fun selectedPollingStation(): LiveData<Pair<Int?, Int?>> = selectedPollingStationLiveData

    fun pollingStation(): LiveData<String> = pollingStationLiveData

    fun getPollingStationBarText() {
        pollingStationLiveData.postValue(
            app.getString(
                R.string.polling_station,
                selectedPollingStationNumber,
                selectedCounty.name
            )
        )
        getSelectedPollingStation()
    }

    @SuppressLint("CheckResult")
    private fun getSelectedPollingStation() {

        var pair: Pair<Int?, Int?> = Pair(null, null)
        var arrivalTime: String? = null
        var departureTime: String? = null
        repository.getPollingStationDetails(selectedCounty.code, selectedPollingStationNumber)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                selectedPollingStationLiveData.postValue(pair)
                setArrivalTime(arrivalTime)
                setDepartureTime(departureTime)
            }
            .subscribe({
                pair = Pair(
                    if (it.urbanArea) R.id.urbanEnvironment else R.id.ruralEnvironment,
                    if (it.isPollingStationPresidentFemale) R.id.femaleGender else R.id.maleGender
                )
                arrivalTime = it.observerArrivalTime
                departureTime = it.observerLeaveTime
            }, {
                onError(it)
            })
    }

    fun validateInputDetails(
        environmentId: Int,
        genderId: Int
    ) {
        when {
            environmentId == -1 -> messageIdToastLiveData.postValue(app.getString(R.string.invalid_polling_station_environment))
            genderId == -1 -> messageIdToastLiveData.postValue(app.getString(R.string.invalid_polling_station_gender))
            !::arrival.isInitialized -> messageIdToastLiveData.postValue(app.getString(R.string.invalid_polling_station_time_in))
            !checkTime() -> messageIdToastLiveData.postValue(app.getString(R.string.invalid_time_input))
            else -> {
                persistSelection(environmentId, genderId)
                nextToMainLiveData.call()
            }
        }
    }

    private fun persistSelection(environmentId: Int, genderId: Int) {
        val pollingStation = PollingStation(
            selectedCounty.code,
            selectedPollingStationNumber,
            environmentId == R.id.urbanEnvironment,
            genderId == R.id.femaleGender,
            arrival.getDateText(),
            departure.getDateText()
        )
        preferences.completedPollingStationConfig()
        repository.savePollingStationDetails(pollingStation)
    }

    private fun checkTime(): Boolean {
        return departure == null || arrival.before(departure)
    }

    fun selectCounty(county: County?) {
        county?.let {
            selectedCounty = it
        }
    }

    private fun validCounty(): Boolean = ::selectedCounty.isInitialized
    fun validPollingStationInput(pollingStationNumberText: CharSequence) {
        if (!validCounty()) {
            messageIdToastLiveData.postValue(app.getString(R.string.invalid_polling_station_county))
            return
        }
        val pollingStationNumber = getPollingStationNumber(pollingStationNumberText)
        when {
            pollingStationNumberText.isEmpty() -> messageIdToastLiveData.postValue(app.getString(R.string.invalid_polling_station_number))
            pollingStationNumber <= 0 -> messageIdToastLiveData.postValue(
                app.getString(R.string.invalid_polling_station_number_minus)
            )
            pollingStationNumber > selectedCounty.limit -> messageIdToastLiveData.postValue(
                app.getString(
                    R.string.invalid_polling_station_number_max,
                    selectedCounty.name,
                    selectedCounty.limit
                )
            )
            else -> {
                selectedPollingStationNumber = pollingStationNumber
                preferences.saveCountyCode(selectedCounty.code)
                preferences.savePollingStationNumber(selectedPollingStationNumber)
                nextLiveData.call()
            }
        }
    }

    private fun getPollingStationNumber(pollingStationNumberText: CharSequence): Int {
        return try {
            Integer.parseInt(pollingStationNumberText.toString())
        } catch (e: NumberFormatException) {
            -1
        }

    }


    fun setArrivalTime(year: Int, month: Int, dayOfMonth: Int, hourOfDay: Int, minute: Int) {
        arrival = Calendar.getInstance()
        arrival.updateTime(year, month, dayOfMonth, hourOfDay, minute)
        arrivalTimeLiveData.postValue(arrival.getTimeText())
    }

    private fun setArrivalTime(time: String?) {
        if (time.isNullOrEmpty()) {
            arrivalTimeLiveData.postValue("")
            return
        }
        val timeInMillis = time.getDate()
        timeInMillis?.let {
            arrival = Calendar.getInstance()
            arrival.timeInMillis = it
            arrivalTimeLiveData.postValue(arrival.getTimeText())
        }
    }

    private fun setDepartureTime(time: String?) {
        if (time.isNullOrEmpty()) {
            departureTimeLiveData.postValue("")
            return
        }
        val timeInMillis = time.getDate()
        timeInMillis?.let {
            departure = Calendar.getInstance()
            departure?.timeInMillis = it
            departureTimeLiveData.postValue(departure?.getTimeText())
        }
    }

    fun setDepartureTime(year: Int, month: Int, dayOfMonth: Int, hourOfDay: Int, minute: Int) {
        departure = Calendar.getInstance()
        departure?.updateTime(year, month, dayOfMonth, hourOfDay, minute)
        departureTimeLiveData.postValue(departure?.getTimeText())
    }

    fun notifyChangeRequested() {
        preferences.completedPollingStationConfig(false)
    }


}