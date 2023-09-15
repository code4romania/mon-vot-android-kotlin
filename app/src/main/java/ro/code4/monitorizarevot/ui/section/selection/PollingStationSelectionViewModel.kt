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
    private val locationLiveData = MutableLiveData<Result<Pair<List<String>, List<String>>>>()

    private val selectionLiveData = SingleLiveEvent<Triple<Int, Int, Int>>()

    fun locations(): LiveData<Result<Pair<List<String>, List<String>>>> = locationLiveData
    fun selection(): LiveData<Triple<Int, Int, Int>> = selectionLiveData

    private val counties: MutableList<County> = mutableListOf()
    private val municipalitiesMap: MutableMap<String, List<Municipality>> = mutableMapOf()

    private var hadSelectedCounty = false
    private var hadSelectedMunicipality = false

    fun getData() {
        locationLiveData.postValue(Result.Loading)

        if (counties.isNotEmpty() && municipalitiesMap.isNotEmpty()) {
            updateData()
            return
        }

        disposables += repository.getData().subscribeOn(Schedulers.io())
            .doOnSuccess {
                counties.clear()
                counties.addAll(it.first)
                counties.sortBy { c -> c.order }

                municipalitiesMap.clear()
                it.second.forEach { m ->
                    val sortedMunicipalities = m.value
                    municipalitiesMap[m.key] = sortedMunicipalities
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                updateData()
            }, {
                onError(it)
            })
    }

    private fun updateData() {
        val countyCode = sharedPreferences.getCountyCode()
        val municipalityCode = sharedPreferences.getMunicipalityCode()
        val pollingStationNumber = sharedPreferences.getPollingStationNumber()
        val countyNames = counties.sortedBy { county -> county.order }.map { it.name }
        val chooseListItem = listOf(app.getString(R.string.polling_station_spinner_choose))

        if (countyCode.isNullOrBlank()) {
            locationLiveData.postValue(
                Result.Success(
                    Pair(chooseListItem + countyNames.toList(), chooseListItem)
                )
            )
            return

        }
        hadSelectedCounty = true
        val municipalitiesNames = municipalitiesMap[countyCode]!!.map { it.name }

        hadSelectedMunicipality = true
        val selectedCountyIndex = counties.indexOfFirst { it.code == countyCode }
        val selectedMunicipalityIndex =
            municipalitiesMap[countyCode]!!.indexOfFirst { it.code == municipalityCode }

        locationLiveData.postValue(Result.Success(Pair(chooseListItem + countyNames.toList(), chooseListItem + municipalitiesNames.toList())))

        if (selectedCountyIndex >= 0 && selectedMunicipalityIndex >= 0) {
            selectionLiveData.postValue(
                Triple(
                    selectedCountyIndex + 1,
                    selectedMunicipalityIndex + 1,
                    pollingStationNumber
                )
            )
        }
    }

    fun getSelectedCounty(position: Int): County? {
        val selectedCounty = counties.getOrNull(position - 1)

        return selectedCounty
    }

    fun getMunicipalitiesForCounty(position: Int): List<String> {
        val selectedCounty = counties.getOrNull(position - 1)
        var municipalitiesNames: List<String> = emptyList()

        if (selectedCounty != null) {
            municipalitiesNames = municipalitiesMap[selectedCounty.code]!!.map { it.name }
        }

        return listOf(app.getString(R.string.polling_station_spinner_choose)) + municipalitiesNames.toList()
    }

    fun getSelectedMunicipality(countyCode: String?, position: Int): Municipality? {
        if (countyCode == null) return null

        return municipalitiesMap
            .getOrElse(countyCode) { emptyList() }
            .getOrNull(position - 1)
    }

    override fun onError(throwable: Throwable) {
        // TODO: Handle errors to show a specific message for each one
        locationLiveData.postValue(Result.Failure(throwable))
    }

    fun hasSelectedStation() =
        sharedPreferences.getHasSelectedStations()
}