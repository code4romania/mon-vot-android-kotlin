package ro.code4.monitorizarevot.ui.base

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.koin.core.inject
import ro.code4.monitorizarevot.helper.getCountyCode
import ro.code4.monitorizarevot.helper.getMunicipalityCode
import ro.code4.monitorizarevot.helper.getPollingStationNumber
import ro.code4.monitorizarevot.helper.getProvinceCode
import ro.code4.monitorizarevot.repositories.Repository

abstract class BaseFormViewModel : BaseViewModel() {
    val repository: Repository by inject()
    val preferences: SharedPreferences by inject()
    var provinceCode: String
    var countyCode: String
    var municipalityCode: String
    var pollingStationNumber: Int = -1
    private val titleLiveData = MutableLiveData<String>()
    fun title(): LiveData<String> = titleLiveData
    fun setTitle(title: String) = titleLiveData.postValue(title)

    init {
        provinceCode = preferences.getProvinceCode()!!
        countyCode = preferences.getCountyCode()!!
        municipalityCode = preferences.getMunicipalityCode()!!
        pollingStationNumber = preferences.getPollingStationNumber()
    }
}