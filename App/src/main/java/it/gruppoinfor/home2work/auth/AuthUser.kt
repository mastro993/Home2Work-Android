package it.gruppoinfor.home2work.auth

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import it.gruppoinfor.home2work.user.User


class AuthUser : User() {

    @SerializedName("AccessToken")
    @Expose
    var accessToken: String = ""
    @SerializedName("Configured")
    @Expose
    var configured: Boolean = false


}