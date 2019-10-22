package ro.code4.monitorizarevot.ui.branch

import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.data.model.BranchDetails
import ro.code4.monitorizarevot.data.model.County
import ro.code4.monitorizarevot.helper.*
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel
import java.util.*


class BranchViewModel : BaseViewModel() {
    private val nextLiveData = SingleLiveEvent<Void>()
    private val nextToMainLiveData = SingleLiveEvent<Void>()
    private val titleLiveData = MutableLiveData<String>()
    private val branchDetailsLiveData = MutableLiveData<String>()
    private val arrivalTimeLiveData = MutableLiveData<String>()
    private val departureTimeLiveData = MutableLiveData<String>()
    private val selectedBranchLiveData = MutableLiveData<Pair<Int?, Int?>>()

    private val preferences: SharedPreferences by inject()
    private val app: Application by inject()
    private val repository: Repository by inject()
    private lateinit var selectedCounty: County
    private var selectedBranchNumber: Int = -1
    private lateinit var arrival: Calendar
    private var departure: Calendar? = null
    fun next(): SingleLiveEvent<Void> = nextLiveData
    fun nextToMain(): SingleLiveEvent<Void> = nextToMainLiveData

    fun title(): LiveData<String> = titleLiveData
    fun setTitle(title: String) = titleLiveData.postValue(title)

    fun arrivalTime(): LiveData<String> = arrivalTimeLiveData
    fun departureTime(): LiveData<String> = departureTimeLiveData
    fun selectedBranch(): LiveData<Pair<Int?, Int?>> = selectedBranchLiveData

    fun branchDetails(): LiveData<String> = branchDetailsLiveData

    fun getBranchBarText() {
        branchDetailsLiveData.postValue(
            app.getString(
                R.string.branch_details,
                selectedBranchNumber,
                selectedCounty.name
            )
        )
        getSelectedBranch()
    }

    @SuppressLint("CheckResult")
    private fun getSelectedBranch() {

        var pair: Pair<Int?, Int?> = Pair(null, null)
        var arrivalTime: String? = null
        var departureTime: String? = null
        repository.getBranch(selectedCounty.code, selectedBranchNumber).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                selectedBranchLiveData.postValue(pair)
                setArrivalTime(arrivalTime)
                setDepartureTime(departureTime)
            }
            .subscribe({
                pair = Pair(
                    if (it.isUrban) R.id.urbanEnvironment else R.id.ruralEnvironment,
                    if (it.isFemale) R.id.femaleGender else R.id.maleGender
                )
                arrivalTime = it.arrivalTime
                departureTime = it.departureTime
            }, {
                onError(it)
            })
    }

    fun validateInputDetails(
        environmentId: Int,
        genderId: Int
    ) {
        when {
            environmentId == -1 -> messageIdToastLiveData.postValue(app.getString(R.string.invalid_branch_environment))
            genderId == -1 -> messageIdToastLiveData.postValue(app.getString(R.string.invalid_branch_gender))
            !::arrival.isInitialized -> messageIdToastLiveData.postValue(app.getString(R.string.invalid_branch_time_in))
            !checkTime() -> messageIdToastLiveData.postValue(app.getString(R.string.invalid_time_input))
            else -> {
                persistSelection(environmentId, genderId)
                nextToMainLiveData.call()
            }
        }
    }

    private fun persistSelection(environmentId: Int, genderId: Int) {
        val branchDetails = BranchDetails(
            selectedCounty.code,
            selectedBranchNumber,
            environmentId == R.id.urbanEnvironment,
            genderId == R.id.femaleGender,
            arrival.getDateText(),
            departure.getDateText()
        )
        preferences.completedBranchConfig()
        repository.saveBranchDetails(branchDetails)
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
    fun validBranchInput(branchNumberText: CharSequence) {
        if (!validCounty()) {
            messageIdToastLiveData.postValue(app.getString(R.string.invalid_branch_county))
            return
        }
        val branchNumber = getBranchNumber(branchNumberText)
        when {
            branchNumberText.isEmpty() -> messageIdToastLiveData.postValue(app.getString(R.string.invalid_branch_number))
            branchNumber <= 0 -> messageIdToastLiveData.postValue(
                app.getString(R.string.invalid_branch_number_minus)
            )
            branchNumber > selectedCounty.branchesCount -> messageIdToastLiveData.postValue(
                app.getString(
                    R.string.invalid_branch_number_max,
                    selectedCounty.name,
                    selectedCounty.branchesCount
                )
            )
            else -> {
                selectedBranchNumber = branchNumber
                preferences.saveCountyCode(selectedCounty.code)
                preferences.saveBranchNumber(selectedBranchNumber)
                nextLiveData.call()
            }
        }
    }

    private fun getBranchNumber(branchNumberText: CharSequence): Int {
        return try {
            Integer.parseInt(branchNumberText.toString())
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
        preferences.completedBranchConfig(false)
    }


}