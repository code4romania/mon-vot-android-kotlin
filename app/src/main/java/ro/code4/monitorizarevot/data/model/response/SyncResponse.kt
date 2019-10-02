package ro.code4.monitorizarevot.data.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SyncResponse {
    @Expose
    @SerializedName("isCompletedSuccessfully")
    var isCompletedSuccessfully = false
}