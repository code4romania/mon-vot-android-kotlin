package ro.code4.monitorizarevot.helper

import android.content.SharedPreferences

const val PREFS_TOKEN = "PREFS_TOKEN"
const val PREFS_LANGUAGE_KEY = "PREFS_LANGUAGE"
const val PREFS_LANGUAGE_EN = "en"
const val PREFS_LANGUAGE_RO = "ro"
const val ACCESS_TOKEN = "access_token"
const val PREFS_COUNTY_CODE = "PREFS_COUNTY_CODE"
const val PREFS_BRANCH_NUMBER = "PREFS_BRANCH_NUMBER"
const val ONBOARDING_COMPLETED = "ONBOARDING_COMPLETED"
fun SharedPreferences.getString(key: String): String? = getString(key, null)
fun SharedPreferences.getInt(key: String): Int = getInt(key, 0)

fun SharedPreferences.putString(key: String, value: String) {
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

fun SharedPreferences.getLanguage(): String =
    getString(PREFS_LANGUAGE_KEY) ?: PREFS_LANGUAGE_RO

fun SharedPreferences.switchLanguage() {
    val lang = if (getLanguage() == PREFS_LANGUAGE_RO) PREFS_LANGUAGE_EN else PREFS_LANGUAGE_RO
    putString(PREFS_LANGUAGE_KEY, lang)
}

fun SharedPreferences.saveCountyCode(countyCode: String?) =
    putString(PREFS_COUNTY_CODE, countyCode ?: "")

fun SharedPreferences.getCountyCode(): String? = getString(PREFS_COUNTY_CODE)
fun SharedPreferences.saveBranchNumber(branchNumber: Int) =
    putInt(PREFS_BRANCH_NUMBER, branchNumber)

fun SharedPreferences.getBranchNumber(): Int = getInt(PREFS_BRANCH_NUMBER)

fun SharedPreferences.hasCompletedOnboarding() = getBoolean(ONBOARDING_COMPLETED, false)
fun SharedPreferences.completedOnboarding() = putBoolean(ONBOARDING_COMPLETED)