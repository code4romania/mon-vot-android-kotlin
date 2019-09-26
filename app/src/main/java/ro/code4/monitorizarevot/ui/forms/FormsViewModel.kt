package ro.code4.monitorizarevot.ui.forms

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.monitorizarevot.adapters.FormAdapter.Companion.TYPE_FORM
import ro.code4.monitorizarevot.adapters.FormAdapter.Companion.TYPE_NOTE
import ro.code4.monitorizarevot.adapters.helper.ListItem
import ro.code4.monitorizarevot.data.pojo.FormWithSections
import ro.code4.monitorizarevot.helper.getBranchNumber
import ro.code4.monitorizarevot.helper.getCountyCode
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel

class FormsViewModel : BaseViewModel() {
    private val repository: Repository by inject()
    private val preferences: SharedPreferences by inject()
    private val formsLiveData = MutableLiveData<ArrayList<ListItem>>()

    init {
        repository.getAnswers(preferences.getCountyCode()!!, preferences.getBranchNumber())
            .observeForever {
            }
        repository.getFormsWithQuestions().observeForever {
            processList(it)
        }
        getForms()
    }

    fun forms(): LiveData<ArrayList<ListItem>> = formsLiveData


    private val branchBarTextLiveData = MutableLiveData<String>()


    fun branchBarText(): LiveData<String> = branchBarTextLiveData

    fun getBranchBarText() {
        branchBarTextLiveData.postValue("${preferences.getCountyCode()} ${preferences.getBranchNumber()}") //todo
    }

    @SuppressLint("CheckResult")
    fun getForms() {

        repository.getForms()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.i("GAGA", "yaya")

            }, {
                onError(it)
            })

    }

    private fun processList(list: List<FormWithSections>) {
        val items = ArrayList<ListItem>()
        items.addAll(list.map { ListItem(TYPE_FORM, it) })
        items.add(ListItem(TYPE_NOTE))
        formsLiveData.postValue(items)
    }

}