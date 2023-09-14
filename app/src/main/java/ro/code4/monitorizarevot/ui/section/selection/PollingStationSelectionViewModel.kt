package ro.code4.monitorizarevot.ui.section.selection

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.data.model.Municipality
import ro.code4.monitorizarevot.data.model.County
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
    private val municipalities: MutableMap<String, List<Municipality>> = mutableMapOf()
    private var selectedCounty: County? = null
    private var selectedMunicipality: Municipality? = null

    private fun hadSelectedCounty(): Boolean {
        return selectedCounty != null
    }
    private fun hadSelectedMunicipality(): Boolean {
        return selectedMunicipality != null
    }

    fun getCounties() {
        countiesLiveData.postValue(Result.Loading)
        if (counties.isNotEmpty()) {
            updateCounties()
            updateMunicipalities()
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
                updateMunicipalities()
            }, {
                onError(it)
            })
    }

    private fun getMunicipalities(countyCode: String) {
        municipalitiesLiveData.postValue(Result.Loading)
        if (municipalities.containsKey(countyCode)) {
            updateMunicipalities()
            return
        }

        disposables += repository.getMunicipalities(countyCode).subscribeOn(Schedulers.io())
            .doOnSuccess {
                municipalities[countyCode] = it.sortedBy { c -> c.order }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                updateMunicipalities()
            }, {
                onError(it)
            })
    }

    private fun updateCounties() {
        selectedCounty = counties.firstOrNull {c ->c.code == sharedPreferences.getCountyCode()}
        val countyNames = counties.sortedBy { county -> county.order }.map { it.name }

        if (hadSelectedCounty()) {
            countiesLiveData.postValue(Result.Success(countyNames.toList()))
        } else {
            countiesLiveData.postValue(Result.Success(listOf(app.getString(R.string.polling_station_spinner_choose)) + countyNames.toList()))
        }

        handleDataChanged()
    }

    private fun updateMunicipalities() {
        var municipalityNames = emptyList<String>()
        if(selectedCounty?.code != null){
           if(municipalities.isEmpty()){
               getMunicipalities(selectedCounty!!.code)
               return
           }

            municipalityNames = municipalities[selectedCounty!!.code]?.map { it.name } ?: emptyList()
        }

        municipalitiesLiveData.postValue(Result.Success(listOf(app.getString(R.string.polling_station_spinner_choose)) + municipalityNames.toList()))
        handleDataChanged()
    }

    private fun handleDataChanged() {
        val pollingStationNumber = sharedPreferences.getPollingStationNumber()
        val countyCode = sharedPreferences.getCountyCode()
        val newMunicipalityCode = sharedPreferences.getMunicipalityCode()

        val selectedCountyIndex = counties.indexOfFirst { it.code == countyCode }
        val selectedMunicipalityIndex = municipalities[countyCode]?.indexOfFirst { it.code == newMunicipalityCode }

        if (selectedCountyIndex >= 0 && selectedMunicipalityIndex !=null && selectedMunicipalityIndex>=0 ) {
            selectionLiveData.postValue(
                Triple(
                    selectedCountyIndex,
                    selectedMunicipalityIndex,
                    pollingStationNumber
                )
            )
        }
    }

    fun selectedCountyAt(position: Int): County? {
        selectedCounty = counties.getOrNull(if (hadSelectedCounty()) position else position - 1)

        if(hadSelectedCounty()){
            getMunicipalities(selectedCounty!!.code)
        }

        return selectedCounty
    }

    fun selectMunicipalityAt(position: Int): Municipality? {
        val countyMunicipalities = when(selectedCounty?.code){
            null, "", app.getString(R.string.polling_station_spinner_choose) -> emptyList()
            else ->  municipalities[selectedCounty!!.code]?: emptyList()
        }

        selectedMunicipality = if(countyMunicipalities.isEmpty()){
            null
        }else{
            countyMunicipalities?.getOrNull(if (hadSelectedMunicipality()) position else position - 1)
        }

        return selectedMunicipality
    }

    override fun onError(throwable: Throwable) {
        // TODO: Handle errors to show a specific message for each one
        countiesLiveData.postValue(Result.Failure(throwable))
    }

    fun hasSelectedStation() =
        sharedPreferences.getHasSelectedStations()


}