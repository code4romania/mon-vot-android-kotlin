package ro.code4.monitorizarevot.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import org.koin.core.KoinComponent
import ro.code4.monitorizarevot.helper.SingleLiveEvent

abstract class BaseViewModel : ViewModel(), KoinComponent {
    val messageIdToastLiveData = SingleLiveEvent<String>()
    val disposables = CompositeDisposable()

    fun messageToast(): LiveData<String> = messageIdToastLiveData
    fun onError(throwable: Throwable) {
        //TODO handle request errors
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}