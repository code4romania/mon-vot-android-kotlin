package ro.code4.monitorizarevot.ui.section

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ro.code4.monitorizarevot.data.pojo.CountyAndPollingStation
import ro.code4.monitorizarevot.helper.Result
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class VisitedPollingStationsViewModel(
    private val repository: Repository
) : BaseViewModel() {
    private val _hasUnsentData = MediatorLiveData<Result<Boolean>>()
    private val _visitedStations = MutableLiveData<Result<List<CountyAndPollingStation>>>()
    val visitedStations: LiveData<Result<List<CountyAndPollingStation>>> = _visitedStations
    val hasUnsentData: LiveData<Result<Boolean>> = _hasUnsentData

    init {
        val subscription = repository.getVisitedStations()
            .subscribeOn(Schedulers.io())
            .map { dbData -> Result.Success(dbData) as Result<List<CountyAndPollingStation>> }
            .startWith(Result.Loading)
            .map {
                if (it is Result.Success) {
                    val sortedData = it.data?.sortedBy { section -> section.observerArrivalTime }
                    Result.Success(sortedData)
                } else {
                    it
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _visitedStations.postValue(it)
            }, { })
        disposables.add(subscription)

        updateSyncStatus()
    }

    private fun updateSyncStatus() {
        val notSyncedQuestionsCount = repository.getNotSyncedQuestions()
        val notSyncedNotesCount = repository.getNotSyncedNotes()
        val notSyncedPollingStationsCount = repository.getNotSyncedPollingStationsCount()
        fun update() {
            if (notSyncedQuestionsCount.value != null && notSyncedNotesCount.value != null
                && notSyncedPollingStationsCount.value != null
            ) {
                _hasUnsentData.value = Result.Success(
                    notSyncedQuestionsCount.value != 0 || notSyncedNotesCount.value != 0 ||
                            notSyncedPollingStationsCount.value != 0
                )
            } else {
                _hasUnsentData.value = Result.Loading
            }
        }
        _hasUnsentData.addSource(notSyncedQuestionsCount) { update() }
        _hasUnsentData.addSource(notSyncedNotesCount) { update() }
        _hasUnsentData.addSource(notSyncedPollingStationsCount) { update() }
    }

    fun sync() {
        repository.syncData()
    }
}

