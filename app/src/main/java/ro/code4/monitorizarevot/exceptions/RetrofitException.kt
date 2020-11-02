package ro.code4.monitorizarevot.exceptions

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException


/**
 * Exception that is retrieved from retrofit. It is of three types, http, network and unexpected.
 */
class RetrofitException internal constructor(
    message: String?,
    /**
     * RobResponse object containing status code, headers, body, etc.
     */
    val response: Response<*>?,
    /**
     * The event kind which triggered this error.
     */
    val kind: Kind,
    val exception: Throwable?,
    /**
     * The Retrofit this request was executed on
     */
    val retrofit: Retrofit?
) :
    RuntimeException(message, exception) {
    /**
     * Identifies the event kind which triggered a [RetrofitException].
     */
    enum class Kind {
        /**
         * An [IOException] occurred while communicating to the server.
         */
        NETWORK,

        /**
         * A non-200 HTTP status code was received from the server.
         */
        HTTP,

        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED
    }

    /**
     * HTTP response body converted to specified `type`. `null` if there is no
     * response.
     *
     * @param type
     * @throws IOException if unable to convert the body to the specified `type`.
     */
    @Throws(IOException::class)
    fun <T> getErrorBodyAs(type: Class<*>?): T? {
        if (response?.errorBody() == null) {
            return null
        }
        val converter: Converter<ResponseBody?, T> =
            retrofit!!.responseBodyConverter(type, arrayOfNulls(0))
        return converter.convert(response.errorBody())
    }

    companion object {
        fun httpError(
            response: Response<*>,
            retrofit: Retrofit?
        ): RetrofitException {
            val message = response.code().toString() + " " + response.message()
            return RetrofitException(
                message,
                response,
                Kind.HTTP,
                null,
                retrofit
            )
        }

        fun networkError(exception: IOException): RetrofitException {
            return RetrofitException(
                exception.message,
                null,
                Kind.NETWORK,
                exception,
                null
            )
        }

        fun unexpectedError(exception: Throwable): RetrofitException {
            return RetrofitException(
                exception.message,
                null,
                Kind.UNEXPECTED,
                exception,
                null
            )
        }
    }

}


