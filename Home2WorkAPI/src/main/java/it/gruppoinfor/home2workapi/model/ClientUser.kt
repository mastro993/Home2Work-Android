package it.gruppoinfor.home2workapi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ClientUser : User(), Serializable {

    @SerializedName("AccessToken")
    @Expose
    var accessToken: String = ""
    @SerializedName("Configured")
    @Expose
    var configured: Boolean = false


}