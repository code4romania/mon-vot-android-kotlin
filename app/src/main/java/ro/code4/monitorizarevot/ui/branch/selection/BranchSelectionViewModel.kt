package ro.code4.monitorizarevot.ui.branch.selection

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.monitorizarevot.App
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.data.model.County
import ro.code4.monitorizarevot.helper.SingleLiveEvent
import ro.code4.monitorizarevot.helper.saveBranchNumber
import ro.code4.monitorizarevot.helper.saveCountyCode
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel


class BranchSelectionViewModel : BaseViewModel() {
    private val repository: Repository by inject()
    private val app: App by inject()
    private val sharedPreferences: SharedPreferences by inject()
    private val countiesLiveData = MutableLiveData<List<County>>()
    private lateinit var selectedCounty: County
    private val nextLiveData = SingleLiveEvent<Boolean>()

    fun counties(): LiveData<List<County>> = countiesLiveData
    fun next(): LiveData<Boolean> = nextLiveData

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

    fun selectCounty(county: County?) {
        county?.let {
            selectedCounty = it
        }
    }

    private fun validCounty(): Boolean = ::selectedCounty.isInitialized
    fun validInput(branchNumberText: CharSequence) {
        if (!validCounty()) {
            messageIdToastLiveData.postValue(app.getString(R.string.invalid_branch_county))
            return
        }
        val branchNumber = getBranchNumber(branchNumberText)
        when {
            branchNumberText.isEmpty() -> messageIdToastLiveData.postValue(app.getString(R.string.invalid_branch_number))
            branchNumber <= 0 -> messageIdToastLiveData.postValue(
                app.getString(R.string.invalid_branch_number_minus)
            )
            branchNumber > selectedCounty.branchesCount -> messageIdToastLiveData.postValue(
                app.getString(
                    R.string.invalid_branch_number_max,
                    selectedCounty.name,
                    selectedCounty.branchesCount
                )
            )
            else -> {
                persistSelection(branchNumber)
                nextLiveData.postValue(true)
            }
        }
    }

    private fun getBranchNumber(branchNumberText: CharSequence): Int {
        return try {
            Integer.parseInt(branchNumberText.toString())
        } catch (e: NumberFormatException) {
            -1
        }

    }

    private fun persistSelection(branchNumber: Int) {
        sharedPreferences.saveCountyCode(selectedCounty.code)
        sharedPreferences.saveBranchNumber(branchNumber)
    }

}