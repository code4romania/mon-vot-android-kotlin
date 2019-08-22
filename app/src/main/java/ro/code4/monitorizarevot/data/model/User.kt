package ro.code4.monitorizarevot.data.model

import com.google.gson.annotations.Expose

class User(
    @field:Expose
    var phone: String?, @field:Expose
    var pin: String?, @field:Expose
    var udid: String?
)