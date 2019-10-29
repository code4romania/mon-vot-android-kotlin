package ro.code4.monitorizarevot.helper

import android.content.SharedPreferences

const val PREFS_TOKEN = "PREFS_TOKEN"
const val PREFS_COUNTY_CODE = "PREFS_COUNTY_CODE"
const val PREFS_POLLING_STATION_NUMBER = "PREFS_POLLING_STATION_NUMBER"
const val ONBOARDING_COMPLETED = "ONBOARDING_COMPLETED"
const val POLLING_STATION_CONFIG_COMPLETED = "POLLING_STATION_CONFIG_COMPLETED"
const val PREFS_LANGUAGE_CODE = "PREFS_LANGUAGE_CODE"
const val RO = "ro"

fun SharedPreferences.getString(key: String): String? = getString(key, null)
fun SharedPreferences.getInt(key: String): Int = getInt(key, 0)

fun SharedPreferences.putString(key: String, value: String?) {
    val editor = edit()
    editor.putString(key, value)
    editor.apply()
}

fun SharedPreferences.putInt(key: String, value: Int) {
    val editor = edit()
    editor.putInt(key, value)
    editor.apply()
}

fun SharedPreferences.putBoolean(key: String, value: Boolean = true) {
    val editor = edit()
    editor.putBoolean(key, value)
    editor.apply()
}

fun SharedPreferences.getToken(): String? = getString(PREFS_TOKEN)
fun SharedPreferences.saveToken(token: String) = putString(PREFS_TOKEN, token)
fun SharedPreferences.deleteToken() = putString(PREFS_TOKEN, null)

fun SharedPreferences.saveCountyCode(countyCode: String?) =
    putString(PREFS_COUNTY_CODE, countyCode.orEmpty())

fun SharedPreferences.getCountyCode(): String? = getString(PREFS_COUNTY_CODE)
fun SharedPreferences.savePollingStationNumber(pollingStationNumber: Int) =
    putInt(PREFS_POLLING_STATION_NUMBER, pollingStationNumber)

fun SharedPreferences.getPollingStationNumber(): Int = getInt(PREFS_POLLING_STATION_NUMBER)
fun SharedPreferences.isPollingStationConfigCompleted() =
    getBoolean(POLLING_STATION_CONFIG_COMPLETED, false)

fun SharedPreferences.completedPollingStationConfig(value: Boolean = true) =
    putBoolean(POLLING_STATION_CONFIG_COMPLETED, value)


fun SharedPreferences.hasCompletedOnboarding() = getBoolean(ONBOARDING_COMPLETED, false)
fun SharedPreferences.completedOnboarding() = putBoolean(ONBOARDING_COMPLETED)

fun SharedPreferences.getLanguage(): String = getString(PREFS_LANGUAGE_CODE, RO) ?: RO
fun SharedPreferences.setLanguage(code: String) = putString(PREFS_LANGUAGE_CODE, code)