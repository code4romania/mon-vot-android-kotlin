package ro.code4.monitorizarevot.ui.branch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ro.code4.monitorizarevot.helper.SingleLiveEvent
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class BranchViewModel : BaseViewModel() {
    private val nextLiveData = SingleLiveEvent<Boolean>()
    private val titleLiveData = MutableLiveData<String>()
    fun next(): LiveData<Boolean> = nextLiveData
    fun goToNextFragment() {
        nextLiveData.postValue(true)
    }

    fun title(): LiveData<String> = titleLiveData
    fun setTitle(title: String) = titleLiveData.postValue(title)
}