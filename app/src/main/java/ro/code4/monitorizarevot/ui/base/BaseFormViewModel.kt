package ro.code4.monitorizarevot.ui.base

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.koin.core.inject
import ro.code4.monitorizarevot.helper.getCommunityCode
import ro.code4.monitorizarevot.helper.getCountyCode
import ro.code4.monitorizarevot.helper.getPollingStationNumber
import ro.code4.monitorizarevot.repositories.Repository

abstract class BaseFormViewModel : BaseViewModel() {
    val repository: Repository by inject()
    val preferences: SharedPreferences by inject()
    var countyCode: String
    var communityCode: String
    var pollingStationNumber: Int = -1
    private val titleLiveData = MutableLiveData<String>()
    fun title(): LiveData<String> = titleLiveData
    fun setTitle(title: String) = titleLiveData.postValue(title)

    init {
        countyCode = preferences.getCountyCode()!!
        communityCode = preferences.getCommunityCode()!!
        pollingStationNumber = preferences.getPollingStationNumber()
    }
}