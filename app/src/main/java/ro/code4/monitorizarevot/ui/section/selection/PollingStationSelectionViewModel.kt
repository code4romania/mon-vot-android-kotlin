package ro.code4.monitorizarevot.ui.section.selection

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.monitorizarevot.data.model.County
import ro.code4.monitorizarevot.helper.*
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel


class PollingStationSelectionViewModel : BaseViewModel() {
    private val repository: Repository by inject()
    private val sharedPreferences: SharedPreferences by inject()
    private val countiesLiveData = MutableLiveData<Result<List<String>>>()

    private val selectionLiveData = SingleLiveEvent<Pair<Int, Int>>()

    fun counties(): LiveData<Result<List<String>>> = countiesLiveData
    fun selection(): LiveData<Pair<Int, Int>> = selectionLiveData

    private val counties: MutableList<County> = mutableListOf()
    private var hadSelectedCounty = false

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
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                updateCounties()
            }, {
                onError(it)
            })
    }

    private fun updateCounties() {
        val countyCode = sharedPreferences.getCountyCode()
        val pollingStationNumber = sharedPreferences.getPollingStationNumber()
        val countyNames = counties.map { it.name }

        if (countyCode.isNullOrBlank()) {
            countiesLiveData.postValue(Result.Success(listOf("") + countyNames))
        } else {
            hadSelectedCounty = true
            val selectedCountyIndex = counties.indexOfFirst { it.code == countyCode }
            countiesLiveData.postValue(Result.Success(countyNames))

            if (selectedCountyIndex >= 0) {
                selectionLiveData.postValue(Pair(selectedCountyIndex, pollingStationNumber))
            }
        }
    }

    fun getSelectedCounty(position: Int): County? {
        return counties.getOrNull(if (hadSelectedCounty) position else position - 1)
    }

    override fun onError(throwable: Throwable) {
        // TODO: Handle errors to show a specific message for each one
        countiesLiveData.postValue(Result.Failure(throwable))
    }


}