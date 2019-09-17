package ro.code4.monitorizarevot.helper

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody


fun String.createMultipart(name: String): MultipartBody.Part {
    val requestBody = toRequestBody("multipart/form-data".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(name, null, requestBody)
}
