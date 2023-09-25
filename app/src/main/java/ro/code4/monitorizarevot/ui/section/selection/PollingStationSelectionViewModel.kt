package ro.code4.monitorizarevot.ui.section.selection

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.data.model.County
import ro.code4.monitorizarevot.data.model.Municipality
import ro.code4.monitorizarevot.data.model.Province
import ro.code4.monitorizarevot.helper.*
import ro.code4.monitorizarevot.repositories.Repository
import ro.code4.monitorizarevot.ui.base.BaseViewModel
import java.io.Serializable


/**
 * Represents a triad of values
 *
 * There is no meaning attached to values in this class, it can be used for any purpose.
 * Triple exhibits value semantics, i.e. two triples are equal if all three components are equal.
 * An example of decomposing it into values:
 *
 * @param A type of the first value.
 * @param B type of the second value.
 * @param C type of the third value.
 * @param D type of the forth value.
 * @property first First value.
 * @property second Second value.
 * @property third Third value.
 * @property forth Forth value.
 */
public data class Quadruple<out A, out B, out C, out D>(
    public val first: A,
    public val second: B,
    public val third: C,
    public val forth: D
) : Serializable {

    /**
     * Returns string representation of the [Quadruple] including its [first], [second], [third] and [forth] values.
     */
    public override fun toString(): String = "($first, $second, $third, $forth)"
}
public fun <T> Quadruple<T, T, T, T>.toList(): List<T> = listOf(first, second, third, forth)


class PollingStationSelectionViewModel : BaseViewModel() {
    private val app: Application by inject()
    private val repository: Repository by inject()
    private val sharedPreferences: SharedPreferences by inject()
    private val provincesLiveData = MutableLiveData<Result<List<String>>>()
    private val countiesLiveData = MutableLiveData<Result<List<String>>>()
    private val municipalitiesLiveData = MutableLiveData<Result<List<String>>>()

    private val selectionLiveData = SingleLiveEvent<Quadruple<Int, Int, Int, Int>>()

    fun provinces(): LiveData<Result<List<String>>> = provincesLiveData
    fun counties(): LiveData<Result<List<String>>> = countiesLiveData
    fun municipalities(): LiveData<Result<List<String>>> = municipalitiesLiveData
    fun selection(): LiveData<Quadruple<Int, Int, Int, Int>> = selectionLiveData

    private val provinces: MutableList<Province> = mutableListOf()
    private val counties: MutableList<County> = mutableListOf()
    private var municipalities: MutableList<Municipality> = mutableListOf()
    private var hadSelectedProvince = false
    private var hadSelectedCounty = false
    private var hadSelectedMunicipality = false

    fun getProvinces() {
        provincesLiveData.postValue(Result.Loading)
        if (provinces.isNotEmpty()) {
            updateProvinces()
            return
        }

        disposables += repository.getProvinces().subscribeOn(Schedulers.io())
            .doOnSuccess {
                provinces.clear()
                provinces.addAll(it)
                provinces.sortBy { c -> c.order }

                counties.clear()
                municipalities.clear()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                updateProvinces()
            }, {
                onError(it)
            })
    }

    fun getCounties(provinceCode: String) {
        disposables += repository.getCounties(provinceCode).subscribeOn(Schedulers.io())
            .doOnSuccess {
                counties.clear()
                counties.addAll(it)
                counties.sortBy { c -> c.order }

                municipalities.clear()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                updateCounties(provinceCode)
            }, {
                onError(it)
            })
    }
    fun getMunicipalities(provinceCode: String, countyCode: String) {
        disposables += repository.getMunicipalities(countyCode).subscribeOn(Schedulers.io())
            .doOnSuccess {
                municipalities.clear()
                municipalities.addAll(it)
                municipalities.sortBy { c -> c.order }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                updateMunicipalities(provinceCode, countyCode)
            }, {
                onError(it)
            })
    }

    private fun updateProvinces() {
        val provinceCode = sharedPreferences.getProvinceCode()

        val provinceNames = provinces.sortedBy { province -> province.order }.map { it.name }

        if (provinceCode.isNullOrBlank()) {
            provincesLiveData.postValue(Result.Success(listOf(app.getString(R.string.polling_station_spinner_choose)) + provinceNames.toList()))
            countiesLiveData.postValue(Result.Success(listOf(app.getString(R.string.polling_station_spinner_choose_county))))
            municipalitiesLiveData.postValue(Result.Success(listOf(app.getString(R.string.polling_station_spinner_choose_municipality))))
            counties.clear()
            municipalities.clear()
            hadSelectedCounty = false
            hadSelectedMunicipality = false
        } else {
            hadSelectedProvince = true
            countiesLiveData.postValue(Result.Success(provinceNames.toList()))

            getCounties(provinceCode)
        }
    }

    private fun updateCounties(provinceCode: String) {
        val countyCode = sharedPreferences.getCountyCode()

        val countyNames = counties.sortedBy { county -> county.order }.map { it.name }

        if (countyCode.isNullOrBlank()) {
            countiesLiveData.postValue(Result.Success(listOf(app.getString(R.string.polling_station_spinner_choose)) + countyNames.toList()))
            municipalitiesLiveData.postValue(Result.Success(listOf(app.getString(R.string.polling_station_spinner_choose_municipality))))
            municipalities.clear()
            hadSelectedMunicipality = false
        } else {
            hadSelectedCounty = true
            countiesLiveData.postValue(Result.Success(countyNames.toList()))

            getMunicipalities(provinceCode, countyCode)
        }
    }

    private fun updateMunicipalities(provinceCode: String, countyCode: String) {
        val municipalityCode = sharedPreferences.getMunicipalityCode()
        val pollingStationNumber = sharedPreferences.getPollingStationNumber()

        val municipalityNamesOfSelectedCounty =
            municipalities.sortedBy { municipality -> municipality.order }.map { it.name }

        if (municipalityCode.isNullOrBlank()) {
            municipalitiesLiveData.postValue(Result.Success(listOf(app.getString(R.string.polling_station_spinner_choose)) + municipalityNamesOfSelectedCounty.toList()))
            hadSelectedMunicipality = false
        } else {
            hadSelectedProvince = true
            hadSelectedCounty = true
            hadSelectedMunicipality = true

            val selectedProvinceIndex = provinces.indexOfFirst { it.code == provinceCode }
            val selectedCountyIndex = counties.indexOfFirst { it.code == countyCode }

            val selectedMunicipalityIndex = municipalities.indexOfFirst { it.code == municipalityCode }
            municipalitiesLiveData.postValue(Result.Success(municipalityNamesOfSelectedCounty.toList()))

            if (selectedCountyIndex >= 0 && selectedMunicipalityIndex >= 0) {
                selectionLiveData.postValue(Quadruple(selectedProvinceIndex, selectedCountyIndex, selectedMunicipalityIndex, pollingStationNumber))
            }
        }
    }

    fun getSelectedProvince(position: Int): Province? {
        return provinces.getOrNull(if (hadSelectedProvince) position else position - 1)
    }

    fun getSelectedCounty(position: Int): County? {
        return counties.getOrNull(if (hadSelectedCounty) position else position - 1)
    }

    fun getSelectedMunicipality(position: Int): Municipality? {
        return municipalities.getOrNull(if (hadSelectedMunicipality) position else position - 1)
    }

    override fun onError(throwable: Throwable) {
        // TODO: Handle errors to show a specific message for each one
        countiesLiveData.postValue(Result.Failure(throwable))
    }

    fun hasSelectedStation() =
        sharedPreferences.getHasSelectedStations()
}