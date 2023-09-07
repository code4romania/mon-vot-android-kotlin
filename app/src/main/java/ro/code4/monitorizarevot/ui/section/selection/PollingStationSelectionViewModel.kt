package ro.code4.monitorizarevot.ui.section.selection

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.data.model.Community
import ro.code4.monitorizarevot.data.model.County
import ro.code4.monitorizarevot.helper.*
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel


class PollingStationSelectionViewModel : BaseViewModel() {
    private val app: Application by inject()
    private val repository: Repository by inject()
    private val sharedPreferences: SharedPreferences by inject()
    private val countiesLiveData = MutableLiveData<Result<List<String>>>()
    private val communitiesLiveData = MutableLiveData<Result<List<String>>>()

    private val selectionLiveData = SingleLiveEvent<Triple<Int, Int, Int>>()

    fun counties(): LiveData<Result<List<String>>> = countiesLiveData
    fun communities(): LiveData<Result<List<String>>> = communitiesLiveData
    fun selection(): LiveData<Triple<Int, Int, Int>> = selectionLiveData

    private val counties: MutableList<County> = mutableListOf()
    private val communities: MutableMap<String, List<Community>> = mutableMapOf()
    private var selectedCounty: County? = null
    private var selectedCommunity: Community? = null

    private fun hadSelectedCounty(): Boolean {
        return selectedCounty != null
    }
    private fun hadSelectedCommunity(): Boolean {
        return selectedCommunity != null
    }

    fun getCounties() {
        countiesLiveData.postValue(Result.Loading)
        if (counties.isNotEmpty()) {
            updateCounties()
            updateCommunities()
            return
        }

        disposables += repository.getCounties().subscribeOn(Schedulers.io())
            .doOnSuccess {
                counties.clear()
                counties.addAll(it)
                counties.sortBy { c -> c.order }

                communities.clear()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                updateCounties()
                updateCommunities()
            }, {
                onError(it)
            })
    }

    private fun getCommunities(countyCode: String) {
        communitiesLiveData.postValue(Result.Loading)
        if (communities.containsKey(countyCode)) {
            updateCommunities()
            return
        }

        disposables += repository.getCommunities(countyCode).subscribeOn(Schedulers.io())
            .doOnSuccess {
                communities[countyCode] = it.sortedBy { c -> c.order }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                updateCommunities()
            }, {
                onError(it)
            })
    }

    private fun updateCounties() {
        selectedCounty = counties.firstOrNull {c ->c.code == sharedPreferences.getCountyCode()}
        val countyNames = counties.sortedBy { county -> county.order }.map { it.name }

        if (hadSelectedCounty()) {
            countiesLiveData.postValue(Result.Success(listOf(app.getString(R.string.polling_station_spinner_choose)) + countyNames.toList()))
        } else {
            countiesLiveData.postValue(Result.Success(countyNames.toList()))

            handleDataChanged()
        }
    }

    private fun updateCommunities() {
        var communityNames = emptyList<String>()
        if(selectedCounty?.code != null){
           if(communities.isEmpty()){
               getCommunities(selectedCounty!!.code)
               return
           }

            communityNames = communities[selectedCounty!!.code]?.map { it.name } ?: emptyList()
        }

        communitiesLiveData.postValue(Result.Success(listOf(app.getString(R.string.polling_station_spinner_choose)) + communityNames.toList()))
        handleDataChanged()
    }

    private fun handleDataChanged() {
        val pollingStationNumber = sharedPreferences.getPollingStationNumber()
        val countyCode = sharedPreferences.getCountyCode()
        val communityCode = sharedPreferences.getCommunityCode()

        val selectedCountyIndex = counties.indexOfFirst { it.code == countyCode }
        val selectedCommunityIndex = communities[countyCode]?.indexOfFirst { it.code == communityCode }

        if (selectedCountyIndex >= 0 &&selectedCommunityIndex !=null && selectedCommunityIndex>=0 ) {
            selectionLiveData.postValue(
                Triple(
                    selectedCountyIndex,
                    selectedCommunityIndex,
                    pollingStationNumber
                )
            )
        }
    }

    fun selectedCountyAt(position: Int): County? {
        selectedCounty = counties.getOrNull(if (hadSelectedCounty()) position else position - 1)

        if(hadSelectedCounty()){
            getCommunities(selectedCounty!!.code)
        }

        return selectedCounty
    }

    fun selectCommunityAt(position: Int): Community? {
        val countyCommunities = when(selectedCounty?.code){
            null, "", app.getString(R.string.polling_station_spinner_choose) -> emptyList()
            else ->  communities[selectedCounty!!.code]?: emptyList()
        }

        selectedCommunity = if(countyCommunities.isEmpty()){
            null
        }else{
            countyCommunities?.getOrNull(if (hadSelectedCommunity()) position else position - 1)

        }

        return selectedCommunity
    }

    override fun onError(throwable: Throwable) {
        // TODO: Handle errors to show a specific message for each one
        countiesLiveData.postValue(Result.Failure(throwable))
    }

    fun hasSelectedStation() =
        sharedPreferences.getHasSelectedStations()


}