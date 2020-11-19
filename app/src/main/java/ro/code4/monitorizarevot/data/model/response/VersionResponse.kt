package ro.code4.monitorizarevot.data.model.response

import com.google.gson.annotations.Expose
import org.parceler.Parcel
import ro.code4.monitorizarevot.data.model.FormDetails

@Parcel(Parcel.Serialization.FIELD)
open class VersionResponse {

    @Expose
    lateinit var formVersions: List<FormDetails>
}

class ErrorVersionResponse(exception: Throwable?) : VersionResponse()