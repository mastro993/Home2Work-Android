package it.gruppoinfor.home2workapi.auth

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import it.gruppoinfor.home2workapi.user.User
import java.io.Serializable


class AuthUser : User(), Serializable {

    @SerializedName("AccessToken")
    @Expose
    var accessToken: String = ""
    @SerializedName("Configured")
    @Expose
    var configured: Boolean = false


}