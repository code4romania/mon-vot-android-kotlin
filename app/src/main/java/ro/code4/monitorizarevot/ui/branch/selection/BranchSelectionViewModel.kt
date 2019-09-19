package ro.code4.monitorizarevot.ui.branch.selection

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.monitorizarevot.data.model.County
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel


class BranchSelectionViewModel : BaseViewModel() {
    private val repository: Repository by inject()
    private val countiesLiveData = MutableLiveData<List<County>>()

    fun counties(): LiveData<List<County>> = countiesLiveData

    @SuppressLint("CheckResult")
    fun getCounties() {
        repository.getCounties().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                countiesLiveData.postValue(it)
            }, {
                onError(it)
            })
    }


}