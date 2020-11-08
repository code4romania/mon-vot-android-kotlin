package ro.code4.monitorizarevot.extensions

import io.reactivex.*
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import ro.code4.monitorizarevot.exceptions.RetrofitException
import ro.code4.monitorizarevot.exceptions.RetrofitException.Companion.httpError
import ro.code4.monitorizarevot.exceptions.RetrofitException.Companion.networkError
import ro.code4.monitorizarevot.exceptions.RetrofitException.Companion.unexpectedError
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Rxjava error handling CallAdapter factory. This class ensures the mapping of errors to
 * one of the following exceptions: http, network or unexpected exceptions.
 */
class RxErrorHandlingCallAdapterFactory private constructor() : CallAdapter.Factory() {
    private val original: RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()

    override fun get(
        returnType: Type, annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *> {
        return RxCallAdapterWrapper(
            returnType,
            retrofit,
            (original.get(returnType, annotations, retrofit) as CallAdapter<Any, Any>)
        )
    }

    internal inner class RxCallAdapterWrapper(
        private val returnType: Type,
        private val retrofit: Retrofit,
        private val wrapped: CallAdapter<Any, Any>
    ) :
        CallAdapter<Any, Any> {
        override fun responseType(): Type {
            return wrapped.responseType()
        }

        override fun adapt(call: Call<Any>): Any? {
            val rawType = getRawType(returnType)

            val isFlowable = rawType == Flowable::class.java
            val isSingle = rawType == Single::class.java
            val isMaybe = rawType == Maybe::class.java
            val isCompletable = rawType == Completable::class.java
            if (rawType != Observable::class.java && !isFlowable && !isSingle && !isMaybe) {
                return null
            }
            if (returnType !is ParameterizedType) {
                val name = if (isFlowable)
                    "Flowable"
                else if (isSingle) "Single" else if (isMaybe) "Maybe" else "Observable"
                throw IllegalStateException(
                    name
                            + " return type must be parameterized"
                            + " as "
                            + name
                            + "<Foo> or "
                            + name
                            + "<? extends Foo>"
                )
            }

            if (isFlowable) {
                return (wrapped.adapt(call) as Flowable<*>).onErrorResumeNext { throwable: Throwable ->
                    Flowable.error(asRetrofitException(throwable))
                }
            }
            if (isSingle) {
                return (wrapped.adapt(call) as Single<*>).onErrorResumeNext { throwable ->
                    Single.error(asRetrofitException(throwable))
                }
            }
            if (isMaybe) {
                return (wrapped.adapt(call) as Maybe<*>).onErrorResumeNext { throwable: Throwable ->
                    Maybe.error(asRetrofitException(throwable))
                }
            }
            if (isCompletable) {
                return (wrapped.adapt(call) as Completable).onErrorResumeNext { throwable ->
                    Completable.error(asRetrofitException(throwable))
                }
            }
            return (wrapped.adapt(call) as Observable<*>).onErrorResumeNext { throwable: Throwable ->
                Observable.error(asRetrofitException(throwable))
            }
        }

        private fun asRetrofitException(throwable: Throwable): RetrofitException {
            return when (throwable) {
                is HttpException -> {
                    val response = throwable.response()
                    httpError(response!!, retrofit)
                }
                is IOException -> {
                    networkError(throwable)
                }
                else -> unexpectedError(throwable)
            }
        }
    }

    companion object {
        const val TAG = "RxErrorHandlingCallAdapterFactory"

        fun create(): CallAdapter.Factory {
            return RxErrorHandlingCallAdapterFactory()
        }
    }
}

