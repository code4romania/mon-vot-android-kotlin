package ro.code4.monitorizarevot.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import ro.code4.monitorizarevot.helper.SingleLiveEvent

abstract class BaseViewModel : ViewModel(), KoinComponent {
    val messageIdToastLiveData = SingleLiveEvent<String>()

    fun messageToast(): LiveData<String> = messageIdToastLiveData
    open fun onError(throwable: Throwable) = Unit
}