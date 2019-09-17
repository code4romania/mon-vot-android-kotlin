package ro.code4.monitorizarevot.data.model.response

import com.google.gson.annotations.SerializedName

class LoginResponse {
    @SerializedName("access_token")
    lateinit var accessToken: String
}