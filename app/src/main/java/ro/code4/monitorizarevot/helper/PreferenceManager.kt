package ro.code4.monitorizarevot.helper

import android.content.SharedPreferences

const val PREFS_TOKEN = "PREFS_TOKEN"
const val ACCESS_TOKEN = "access_token"
fun SharedPreferences.getString(key: String): String? = getString(key, null)
fun SharedPreferences.putString(key: String, value: String) {
    val editor = edit()
    editor.putString(key, value)
    editor.apply()
}


fun SharedPreferences.getToken(): String? = getString(PREFS_TOKEN)
fun SharedPreferences.putToken(token: String) = putString(PREFS_TOKEN, token)