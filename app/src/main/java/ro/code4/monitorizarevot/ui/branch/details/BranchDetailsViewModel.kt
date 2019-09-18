package ro.code4.monitorizarevot.ui.branch.details

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import org.koin.core.inject
import ro.code4.monitorizarevot.App
import ro.code4.monitorizarevot.data.model.County
import ro.code4.monitorizarevot.helper.SingleLiveEvent
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class BranchDetailsViewModel : BaseViewModel() {
    private val repository: Repository by inject()
    private val app: App by inject()
    private val sharedPreferences: SharedPreferences by inject()
    private val countiesLiveData = MutableLiveData<List<County>>()
    private lateinit var selectedCounty: County
    private val nextLiveData = SingleLiveEvent<Boolean>()
}