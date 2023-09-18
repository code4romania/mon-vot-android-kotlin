package ro.code4.monitorizarevot.ui.section.selection

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
import ro.code4.monitorizarevot.helper.*
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel


class PollingStationSelectionViewModel : BaseViewModel() {
    private val app: Application by inject()
    private val repository: Repository by inject()
    private val sharedPreferences: SharedPreferences by inject()
    private val countiesLiveData = MutableLiveData<Result<List<String>>>()
    private val municipalitiesLiveData = MutableLiveData<Result<List<String>>>()

    private val selectionLiveData = SingleLiveEvent<Triple<Int, Int, Int>>()

    fun counties(): LiveData<Result<List<String>>> = countiesLiveData
    fun municipalities(): LiveData<Result<List<String>>> = municipalitiesLiveData
    fun selection(): LiveData<Triple<Int, Int, Int>> = selectionLiveData

    private val counties: MutableList<County> = mutableListOf()
    private var municipalities: MutableList<Municipality> = mutableListOf()
    private var hadSelectedCounty = false
    private var hadSelectedMunicipality = false

    fun getCounties() {
        countiesLiveData.postValue(Result.Loading)
        if (counties.isNotEmpty()) {
            updateCounties()
            return
        }

        disposables += repository.getCounties().subscribeOn(Schedulers.io())
            .doOnSuccess {
                counties.clear()
                counties.addAll(it)
                counties.sortBy { c -> c.order }

                municipalities.clear()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                updateCounties()
            }, {
                onError(it)
            })
    }

    fun getMunicipalities(countyCode: String) {
        disposables += repository.getMunicipalities(countyCode).subscribeOn(Schedulers.io())
            .doOnSuccess {
                municipalities.clear()
                municipalities.addAll(it)
                municipalities.sortBy { c -> c.order }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                updateMunicipalities(countyCode)
            }, {
                onError(it)
            })
    }

    private fun updateCounties() {
        val countyCode = sharedPreferences.getCountyCode()

        val countyNames = counties.sortedBy { county -> county.order }.map { it.name }

        if (countyCode.isNullOrBlank()) {
            countiesLiveData.postValue(Result.Success(listOf(app.getString(R.string.polling_station_spinner_choose)) + countyNames.toList()))
            municipalitiesLiveData.postValue(Result.Success(listOf(app.getString(R.string.polling_station_spinner_choose_municipality))))
            municipalities.clear()
            hadSelectedMunicipality = false
        } else {
            hadSelectedCounty = true
            countiesLiveData.postValue(Result.Success(countyNames.toList()))

            getMunicipalities(countyCode)
        }
    }

    private fun updateMunicipalities(countyCode: String) {
        val municipalityCode = sharedPreferences.getMunicipalityCode()
        val pollingStationNumber = sharedPreferences.getPollingStationNumber()

        val municipalityNamesOfSelectedCounty =
            municipalities.sortedBy { municipality -> municipality.order }.map { it.name }

        if (municipalityCode.isNullOrBlank()) {
            municipalitiesLiveData.postValue(Result.Success(listOf(app.getString(R.string.polling_station_spinner_choose)) + municipalityNamesOfSelectedCounty.toList()))
            hadSelectedMunicipality = false
        } else {
            hadSelectedCounty = true
            hadSelectedMunicipality = true

            val selectedCountyIndex = counties.indexOfFirst { it.code == countyCode }

            val selectedMunicipalityIndex = municipalities.indexOfFirst { it.code == municipalityCode }
            municipalitiesLiveData.postValue(Result.Success(municipalityNamesOfSelectedCounty.toList()))

            if (selectedCountyIndex >= 0 && selectedMunicipalityIndex >= 0) {
                selectionLiveData.postValue(Triple(selectedCountyIndex, selectedMunicipalityIndex, pollingStationNumber))
            }
        }
    }

    fun getSelectedCounty(position: Int): County? {
        return counties.getOrNull(if (hadSelectedCounty) position else position - 1)
    }

    fun getSelectedMunicipality(position: Int): Municipality? {
        return municipalities.getOrNull(if (hadSelectedMunicipality) position else position - 1)
    }

    override fun onError(throwable: Throwable) {
        // TODO: Handle errors to show a specific message for each one
        countiesLiveData.postValue(Result.Failure(throwable))
    }

    fun hasSelectedStation() =
        sharedPreferences.getHasSelectedStations()
}