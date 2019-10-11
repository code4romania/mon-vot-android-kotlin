package ro.code4.monitorizarevot.ui.branch.selection

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.monitorizarevot.data.model.County
import ro.code4.monitorizarevot.helper.SingleLiveEvent
import ro.code4.monitorizarevot.helper.getBranchNumber
import ro.code4.monitorizarevot.helper.getCountyCode
import ro.code4.monitorizarevot.helper.plusAssign
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel


class BranchSelectionViewModel : BaseViewModel() {
    private val repository: Repository by inject()
    private val sharedPreferences: SharedPreferences by inject()
    private val countiesLiveData = MutableLiveData<List<String>>()

    private val selectionLiveData = SingleLiveEvent<Pair<Int, Int>>()

    fun counties(): LiveData<List<String>> = countiesLiveData
    fun selection(): LiveData<Pair<Int, Int>> = selectionLiveData

    private val counties: MutableList<County> = mutableListOf()
    private var hadSelectedCounty = false

    fun getCounties() {
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
        val branchNumber = sharedPreferences.getBranchNumber()
        val countyNames = counties.map { it.name.orEmpty() }

        if (countyCode.isNullOrBlank()) {
            countiesLiveData.postValue(listOf("") + countyNames)
        } else {
            hadSelectedCounty = true
            val selectedCountyIndex = counties.indexOfFirst { it.code == countyCode }
            countiesLiveData.postValue(countyNames)

            if (selectedCountyIndex >= 0) {
                selectionLiveData.postValue(Pair(selectedCountyIndex, branchNumber))
            }
        }
    }

    fun getSelectedCounty(position: Int): County? {
        return counties.getOrNull(if (hadSelectedCounty) position else position - 1)
    }
}