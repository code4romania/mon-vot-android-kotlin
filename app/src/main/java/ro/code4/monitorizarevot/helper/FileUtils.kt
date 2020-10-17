package ro.code4.monitorizarevot.helper

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File
import java.io.IOException


/**
 * File utilities
 */
object FileUtils {
    private const val UPLOADS_DIR_NAME = "uploads"

    @Throws(IOException::class)
    internal fun copyFileToCache(context: Context, uri: Uri): File =
        with(context) {
            val directory = File(cacheDir, UPLOADS_DIR_NAME)
            directory.mkdirs()
            File(
                directory,
                getFileName(uri) ?: generateTempFileName(uri)
            ).also { f ->
                contentResolver.openInputStream(uri)?.use {
                    f.createNewFile()
                    it.copyTo(f.outputStream())
                }
            }
        }

    private fun Context.getFileName(uri: Uri): String? =
        when (uri.scheme) {
            ContentResolver.SCHEME_FILE -> uri.path?.let { File(it).name }
            ContentResolver.SCHEME_CONTENT -> getCursorContent(uri)
            else -> null
        }

    private fun Context.getCursorContent(uri: Uri): String? =
        runCatching {
            contentResolver.query(uri, null, null, null, null)
                ?.let { cursor ->
                    cursor.run {
                        if (moveToFirst())
                            getString(getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        else null
                    }.also { cursor.close() }
                }
        }.getOrNull()

    private fun Context.generateTempFileName(uri: Uri): String =
        "mon_vot_${System.currentTimeMillis()}.${getMimeType(contentResolver, uri)}"

    private fun getMimeType(contentResolver: ContentResolver, uri: Uri): String? =
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
        } else {
            MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        }
}
