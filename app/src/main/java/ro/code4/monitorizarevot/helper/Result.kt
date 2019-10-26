package ro.code4.monitorizarevot.helper

/**
 * .:.:.:. Created by @henrikhorbovyi on 13/10/19 .:.:.:.
 */
sealed class Result<out T> {
    class Failure(val error: Throwable, val message: String = "") : Result<Nothing>()
    class Success<out T>(val data: T? = null) : Result<T>()
    object Loading : Result<Nothing>()


    fun handle(
        onSuccess: (T?) -> Unit = {},
        onFailure: (Throwable) -> Unit = {},
        onLoading: () -> Unit = {}
    ) {
        when (this) {
            is Success -> onSuccess(data)
            is Failure -> onFailure(error)
            is Loading -> onLoading()
        }
    }
}