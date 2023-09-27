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
import ro.code4.monitorizarevot.data.model.Municipality
import ro.code4.monitorizarevot.data.model.PollingStation
import ro.code4.monitorizarevot.data.model.Province
import ro.code4.monitorizarevot.helper.*
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel
import java.util.*

data class PollingStationData(
    val numberOfVotersOnTheList: Int? ,
    val numberOfCommissionMembers: Int? ,
    val numberOfFemaleMembers: Int? ,
    val minPresentMembers: Int? ,
    val chairmanPresence: Int?,
    val singlePollingStationOrCommission: Int?,
    val adequatePollingStationSize: Int?)
class PollingStationViewModel : BaseViewModel() {
    private val nextLiveData = SingleLiveEvent<Void>()
    private val nextToMainLiveData = SingleLiveEvent<Void>()
    private val titleLiveData = MutableLiveData<String>()
    private val pollingStationLiveData = MutableLiveData<String>()
    private val arrivalTimeLiveData = MutableLiveData<String>()
    private val departureTimeLiveData = MutableLiveData<String>()
    private val selectedPollingStationLiveData = MutableLiveData<PollingStationData>()

    private val preferences: SharedPreferences by inject()
    private val app: Application by inject()
    private val repository: Repository by inject()
    private lateinit var selectedProvince: Province
    private var selectedCounty: County? = null
    private var selectedMunicipality: Municipality? = null
    private var selectedPollingStationNumber: Int = -1
    private lateinit var arrival: Calendar
    private var departure: Calendar? = null
    fun next(): SingleLiveEvent<Void> = nextLiveData
    fun nextToMain(): SingleLiveEvent<Void> = nextToMainLiveData

    fun title(): LiveData<String> = titleLiveData
    fun setTitle(title: String) = titleLiveData.postValue(title)

    fun arrivalTime(): LiveData<String> = arrivalTimeLiveData
    fun departureTime(): LiveData<String> = departureTimeLiveData
    fun selectedPollingStation(): LiveData<PollingStationData> = selectedPollingStationLiveData

    fun pollingStation(): LiveData<String> = pollingStationLiveData

    fun getPollingStationBarText() {
        pollingStationLiveData.postValue(
            app.getString(
                R.string.polling_station,
                selectedPollingStationNumber,
                selectedProvince.name,
                selectedCounty!!.name,
                selectedMunicipality!!.name
            )
        )
        getSelectedPollingStation()
    }

    @SuppressLint("CheckResult")
    private fun getSelectedPollingStation() {
        var numberOfVotersOnTheList: Int? = null
        var numberOfCommissionMembers: Int? = null
        var numberOfFemaleMembers: Int? = null
        var minPresentMembers: Int? = null
        var chairmanPresence: Int? = null
        var singlePollingStationOrCommission: Int? = null
        var adequatePollingStationSize: Int? = null

        var arrivalTime: String? = null
        var departureTime: String? = null
        repository.getPollingStationDetails(selectedCounty!!.code, selectedMunicipality!!.code, selectedPollingStationNumber)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                val data = PollingStationData(
                    numberOfVotersOnTheList,
                    numberOfCommissionMembers,
                    numberOfFemaleMembers,
                    minPresentMembers,
                    chairmanPresence,
                    singlePollingStationOrCommission,
                    adequatePollingStationSize)

                selectedPollingStationLiveData.postValue(data)
                setArrivalTime(arrivalTime)
                setDepartureTime(departureTime)
            }
            .subscribe({
                arrivalTime = it.observerArrivalTime
                departureTime = it.observerLeaveTime
                numberOfVotersOnTheList = it.numberOfVotersOnTheList
                numberOfCommissionMembers = it.numberOfCommissionMembers
                numberOfFemaleMembers = it.numberOfFemaleMembers
                minPresentMembers = it.minPresentMembers
                chairmanPresence = if (it.chairmanPresence) R.id.chairmanPresenceYes else R.id.chairmanPresenceNo
                singlePollingStationOrCommission = if (it.singlePollingStationOrCommission) R.id.singlePollingStationOrCommissionYes else R.id.singlePollingStationOrCommissionNo
                adequatePollingStationSize = if (it.adequatePollingStationSize) R.id.adequatePollingStationSizeYes else R.id.adequatePollingStationSizeNo

            }, {
                onError(it)
            })
    }

    fun validateInputDetails(
        numberOfVotersOnTheList: CharSequence,
        numberOfCommissionMembers: CharSequence,
        numberOfFemaleMembers: CharSequence,
        minPresentMembers: CharSequence,
        chairmanPresenceId: Int,
        singlePollingStationOrCommissionId: Int,
        adequatePollingStationSizeId: Int

    ) {
        when {
            numberOfVotersOnTheList.isEmpty() || toInt(numberOfVotersOnTheList) == -1 -> messageIdToastLiveData.postValue(app.getString(R.string.invalid_polling_station_number_of_voters_on_the_list))
            numberOfCommissionMembers.isEmpty() || toInt(numberOfCommissionMembers) == -1-> messageIdToastLiveData.postValue(app.getString(R.string.invalid_polling_station_number_of_commission_members))
            numberOfFemaleMembers.isEmpty()|| toInt(numberOfFemaleMembers) == -1 ->messageIdToastLiveData.postValue(app.getString(R.string.invalid_polling_station_number_of_female_members))
            minPresentMembers.isEmpty()|| toInt(minPresentMembers) == -1 -> messageIdToastLiveData.postValue(app.getString(R.string.invalid_polling_station_min_present_members))

            chairmanPresenceId == -1 -> messageIdToastLiveData.postValue(app.getString(R.string.invalid_polling_station_chairman_presence))
            singlePollingStationOrCommissionId == -1 -> messageIdToastLiveData.postValue(app.getString(R.string.invalid_polling_station_single_commission))
            adequatePollingStationSizeId == -1 -> messageIdToastLiveData.postValue(app.getString(R.string.invalid_polling_station_adequate_size))
            !::arrival.isInitialized -> messageIdToastLiveData.postValue(app.getString(R.string.invalid_polling_station_time_in))
            !checkTime() -> messageIdToastLiveData.postValue(app.getString(R.string.invalid_time_input))
            else -> {
                persistSelection(toInt(numberOfVotersOnTheList),
                    toInt(numberOfCommissionMembers),
                    toInt(numberOfFemaleMembers),
                    toInt(minPresentMembers),
                    chairmanPresenceId,
                    singlePollingStationOrCommissionId,
                    adequatePollingStationSizeId)
                nextToMainLiveData.call()
            }
        }
    }

    private fun persistSelection(
        numberOfVotersOnTheList: Int,
        numberOfCommissionMembers: Int,
        numberOfFemaleMembers: Int,
        minPresentMembers: Int,
        chairmanPresenceId: Int,
        singlePollingStationOrCommissionId: Int,
        adequatePollingStationSizeId: Int) {
        val pollingStation = PollingStation(
            selectedProvince!!.code,
            selectedCounty!!.code,
            selectedMunicipality!!.code,
            selectedPollingStationNumber,
            arrival.getDateISO8601Text(),
            departure.getDateISO8601Text(),
            numberOfVotersOnTheList,
            numberOfCommissionMembers,
            numberOfFemaleMembers,
            minPresentMembers,
            chairmanPresenceId== R.id.chairmanPresenceYes,
            singlePollingStationOrCommissionId  == R.id.singlePollingStationOrCommissionYes,
            adequatePollingStationSizeId  == R.id.adequatePollingStationSizeYes
        )
        preferences.completedPollingStationConfig()
        repository.savePollingStationDetails(pollingStation)
    }

    private fun checkTime(): Boolean {
        return departure == null || arrival.before(departure)
    }

    fun selectProvince(province: Province?) {
        province?.let {
            selectedProvince = it
        }
    }
    fun selectCounty(county: County?) {
        county?.let {
            selectedCounty = it
        }
    }

    fun deselectCounty() {
        selectedCounty = null
    }
    fun deselectMunicipality() {
        selectedMunicipality = null
    }

    fun selectMunicipality(municipality: Municipality?) {
        municipality?.let {
            selectedMunicipality = it
        }
    }

    private fun validProvince(): Boolean = ::selectedProvince.isInitialized
    private fun validCounty(): Boolean  { return selectedCounty != null }
    private fun validMunicipality(): Boolean { return selectedMunicipality != null }

    fun validPollingStationInput(pollingStationNumberText: CharSequence) {
        if (!validProvince()) {
            messageIdToastLiveData.postValue(app.getString(R.string.invalid_polling_station_province))
            return
        }

        if (!validCounty()) {
            messageIdToastLiveData.postValue(app.getString(R.string.invalid_polling_station_county))
            return
        }

        if (!validMunicipality()) {
            messageIdToastLiveData.postValue(app.getString(R.string.invalid_polling_station_municipality))
            return
        }

        val pollingStationNumber = toInt(pollingStationNumberText)
        when {
            pollingStationNumberText.isEmpty() -> messageIdToastLiveData.postValue(app.getString(R.string.invalid_polling_station_number))
            pollingStationNumber <= 0 -> messageIdToastLiveData.postValue(
                app.getString(R.string.invalid_polling_station_number_minus)
            )
            pollingStationNumber > selectedMunicipality!!.limit -> messageIdToastLiveData.postValue(
                app.getString(
                    R.string.invalid_polling_station_number_max,
                    selectedProvince.name,
                    selectedCounty!!.name,
                    selectedMunicipality!!.name,
                    selectedMunicipality!!.limit
                )
            )
            else -> {
                selectedPollingStationNumber = pollingStationNumber
                preferences.saveProvinceCode(selectedProvince.code)
                preferences.saveCountyCode(selectedCounty!!.code)
                preferences.saveMunicipalityCode(selectedMunicipality!!.code)
                preferences.savePollingStationNumber(selectedPollingStationNumber)
                nextLiveData.call()
            }
        }
    }

    private fun toInt(text: CharSequence, default: Int = -1): Int {
        return try {
            Integer.parseInt(text.toString())
        } catch (e: NumberFormatException) {
            default
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

    fun registerStationSelection() {
        preferences.setHasSelectedStations(true)
    }
}