package ro.code4.monitorizarevot.data.model

import com.google.gson.annotations.Expose

class User(
    @field:Expose var user: String,
    @field:Expose var password: String,
    @field:Expose var fcmToken: String,
    @field:Expose var channelName: String = "Firebase"
)