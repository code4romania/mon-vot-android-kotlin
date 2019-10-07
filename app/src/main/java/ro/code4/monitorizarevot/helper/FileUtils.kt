package ro.code4.monitorizarevot.helper

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log


/**
 * File utilities, thanks to https://github.com/epforgpl/monitorizare-vot-android/
 */
object FileUtils {
    /** TAG for log messages.  */
    internal const val TAG = "FileUtils"
    private const val DEBUG = false // Set to true to enable logging

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        uri?.let {
            try {
                cursor =
                    context.contentResolver.query(it, projection, selection, selectionArgs, null)
                cursor?.let { curs ->
                    @Suppress("ConstantConditionIf")
                    if (curs.moveToFirst()) {
                        if (DEBUG)
                            DatabaseUtils.dumpCursor(curs)

                        val columnIndex = curs.getColumnIndexOrThrow(column)
                        return curs.getString(columnIndex)
                    }
                }
            } finally {
                cursor?.close()
            }
        }
        return null
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br></br>
     * <br></br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     */
    fun getPath(context: Context, uri: Uri): String? {

        @Suppress("ConstantConditionIf")
        if (DEBUG)
            Log.d(
                "$TAG File -",
                "Authority: " + uri.authority +
                        ", Fragment: " + uri.fragment +
                        ", Port: " + uri.port +
                        ", Query: " + uri.query +
                        ", Scheme: " + uri.scheme +
                        ", Host: " + uri.host +
                        ", Segments: " + uri.pathSegments.toString()
            )

        // DocumentProvider
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split =
                docId.split((":").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]

            if ("primary".equals(type, ignoreCase = true)) {
                return "${context.getExternalFilesDir(null)}/${split[1]}"
            }

            // TODO handle non-primary volumes
        } else if (isDownloadsDocument(uri)) {

            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
            )

            return getDataColumn(context, contentUri, null, null)
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split =
                docId.split((":").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]

            var contentUri: Uri? = null
            @Suppress("ConstantConditionIf")
            when (type) {
                "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])

            return getDataColumn(context, contentUri, selection, selectionArgs)
        }// MediaProvider
        // DownloadsProvider
        // File
        // MediaStore (and general)

        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}
