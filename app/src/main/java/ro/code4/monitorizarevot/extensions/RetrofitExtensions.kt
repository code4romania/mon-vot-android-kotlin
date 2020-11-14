package ro.code4.monitorizarevot.extensions

import retrofit2.HttpException
import retrofit2.Response

fun <T> Response<T>.successOrThrow(): Boolean {
    if (!isSuccessful) throw HttpException(this)
    return true
}