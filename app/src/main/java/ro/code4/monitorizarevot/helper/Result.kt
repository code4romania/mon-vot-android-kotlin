package ro.code4.monitorizarevot.helper


/**

 * Class that encapsulates successful result with a value of type [T] or
 * a failure with a [Throwable] exception.
 */
sealed class Result<out T> {
    data class Error(val exception: Throwable) : Result<Nothing>()
    data class Success<out T>(val data: T? = null) : Result<T>()
    object Loading : Result<Nothing>()

    fun exceptionOrNull(): Throwable? =
        when (this) {
            is Error -> exception
            else -> null
        }

    fun handle(
        onSuccess: (T?) -> Unit = {},
        onFailure: (Throwable) -> Unit = {},
        onLoading: () -> Unit = {}
    ) {
        when (this) {
            is Success -> onSuccess(data)
            is Error -> onFailure(exception)
            is Loading -> onLoading()
        }
    }

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}

val Result<*>.succeeded
    get() = this is Result.Success && data != null

val Result<*>.error
    get() = this is Result.Error

inline fun <R, T : R> Result<T>.getOrThrow(onFailure: (exception: Throwable) -> R): R {
    return when (val exception = exceptionOrNull()) {
        null -> data as T
        else -> onFailure(exception)
    }
}

val <T> Result<T>.data: T?
    get() = (this as? Result.Success)?.data
