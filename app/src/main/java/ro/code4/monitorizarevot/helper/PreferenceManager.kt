package ro.code4.monitorizarevot.helper

import android.content.SharedPreferences
import ro.code4.monitorizarevot.BuildConfig

const val PREFS_TOKEN = "PREFS_TOKEN"
const val PREFS_COUNTY_CODE = "PREFS_COUNTY_CODE"
const val PREFS_POLLING_STATION_NUMBER = "PREFS_POLLING_STATION_NUMBER"
const val ONBOARDING_COMPLETED = "ONBOARDING_COMPLETED"
const val POLLING_STATION_CONFIG_COMPLETED = "POLLING_STATION_CONFIG_COMPLETED"
const val PREFS_LANGUAGE_CODE = "PREFS_LANGUAGE_CODE"
const val PREFS_LAST_DB_RESET_TIMESTAMP = "PREFS_LAST_DB_RESET_TIMESTAMP"


fun SharedPreferences.getString(key: String): String? = getString(key, null)
fun SharedPreferences.getInt(key: String): Int = getInt(key, 0)
fun SharedPreferences.getLong(key: String): Long = getLong(key, 0)

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

fun SharedPreferences.putLong(key: String, value: Long) {
    val editor = edit()
    editor.putLong(key, value)
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

fun SharedPreferences.getLocaleCode(): String =
    getString(PREFS_LANGUAGE_CODE, BuildConfig.PREFERRED_LOCALE) ?: BuildConfig.PREFERRED_LOCALE
fun SharedPreferences.setLocaleCode(code: String) = putString(PREFS_LANGUAGE_CODE, code)

fun SharedPreferences.getLastDbResetTimestamp() = getLong(PREFS_LAST_DB_RESET_TIMESTAMP)
fun SharedPreferences.setLastDbResetTimestamp(value: Long) = putLong(PREFS_LAST_DB_RESET_TIMESTAMP, value)

fun SharedPreferences.clearUserPrefs() = run {
    completedPollingStationConfig(false)
    removeCurrentLocationPrefs()
    deleteToken()
}

private fun SharedPreferences.removeCurrentLocationPrefs() {
    val editor = edit()
    editor.remove(PREFS_COUNTY_CODE)
    editor.remove(PREFS_POLLING_STATION_NUMBER)
    editor.apply()
}