package ro.code4.monitorizarevot.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Parcel(Parcel.Serialization.FIELD)
class FormDetails {
    //    @PrimaryKey
    @Expose
    @SerializedName("code")
    var code: String? = null

    @Expose
    @SerializedName("description")
    var description: String? = null

    @Expose
    @SerializedName("ver")
    var formVersion: Int = 0

    constructor() {

    }

    constructor(code: String, description: String, formVersion: Int) {
        this.code = code
        this.description = description
        this.formVersion = formVersion
    }

//    override fun equals(o: Any?): Boolean {
//        if (this === o) return true
//        if (o ) return false
//
//        val that = o as FormDetails?
//
//        if (formVersion != that!!.formVersion) return false
//        if (if (code != null) code != that.code else that.code != null) return false
//        return if (description != null) description == that.description else that.description == null
//    }
//
//    fun hashCode(): Int {
//        var result = if (code != null) code!!.hashCode() else 0
//        result = 31 * result + if (description != null) description!!.hashCode() else 0
//        result = 31 * result + formVersion
//        return result
//    }
}