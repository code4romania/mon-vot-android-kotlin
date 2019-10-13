package ro.code4.monitorizarevot.ui.branch.selection

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.monitorizarevot.data.model.County
import ro.code4.monitorizarevot.helper.Result
import ro.code4.monitorizarevot.helper.SingleLiveEvent
import ro.code4.monitorizarevot.helper.getBranchNumber
import ro.code4.monitorizarevot.helper.getCountyCode
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel


class BranchSelectionViewModel : BaseViewModel() {

    private val repository: Repository by inject()
    private val sharedPreferences: SharedPreferences by inject()
    private val countiesLiveData = MutableLiveData<Result<List<County>>>()
    private val selectionLiveData = SingleLiveEvent<Pair<County, Int>>()

    init {
        getCounties()
    }

    fun counties(): LiveData<Result<List<County>>> = countiesLiveData
    fun selection(): LiveData<Pair<County, Int>> = selectionLiveData

    @SuppressLint("CheckResult")
    fun getCounties() {
        countiesLiveData.postValue(Result.Loading)
        repository.getCounties().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                countiesLiveData.postValue(Result.Success(it))
            }, {
                onError(it)
            })
    }

    fun getSelection() {
        val countyCode = sharedPreferences.getCountyCode()
        val branchNumber = sharedPreferences.getBranchNumber()
        countyCode?.let {
            repository.getCounty(countyCode).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ county ->
                    selectionLiveData.postValue(Pair(county, branchNumber))
                }, {
                    onError(it)
                })
        }
    }

    override fun onError(throwable: Throwable) {
        // TODO: Handle errors to show a specific message for each one
        countiesLiveData.postValue(Result.Failure(throwable))
    }


}