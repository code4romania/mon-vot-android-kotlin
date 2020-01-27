package ro.code4.monitorizarevot.helper

import android.util.Log

/**
 * Utility class to easier use the [Log] class in Kotlin files
 * It takes by default the classes' canonical name or uses [StringUtils.EMPTY],
 * if the class is null or it is used outside of a class
 *
 * @author Sebastian Gansca on 24.11.2019
 */
fun Any.logV(message: String, tag: String? = null) = Log.v(buildTag(this::class.java.name, tag), message)

fun Any.logD(message: String, tag: String? = null) = Log.d(buildTag(this::class.java.name, tag), message)

fun Any.logI(message: String, tag: String? = null) = Log.i(buildTag(this::class.java.name, tag), message)

fun Any.logW(message: String, tag: String? = null) = Log.w(buildTag(this::class.java.name, tag), message)

fun Any.logW(message: String, error: Throwable, tag: String? = null) = Log.w(buildTag(this::class.java.name, tag), message, error)

fun Any.logE(message: String, tag: String? = null) = Log.e(buildTag(this::class.java.name, tag), message)

fun Any.logE(message: String, error: Throwable, tag: String? = null) = Log.e(buildTag(this::class.java.name, tag), message, error)

private fun buildTag(className: String?, tag: String?): String = when {
    !className.isNullOrEmpty() -> className!!
    else -> tag ?: "ro.code4.monitorizarevot.default"
}