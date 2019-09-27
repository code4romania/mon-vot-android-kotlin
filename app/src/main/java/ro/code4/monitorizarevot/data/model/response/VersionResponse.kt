package ro.code4.monitorizarevot.data.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel
import ro.code4.monitorizarevot.data.model.FormDetails

@Parcel(Parcel.Serialization.FIELD)
class VersionResponse {

    // TODO serialized names to be translated when api is updated
    @Expose
    @SerializedName("formulare")
    lateinit var formDetailsList: List<FormDetails>
}