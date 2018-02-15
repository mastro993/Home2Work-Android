package it.gruppoinfor.home2workapi.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class UserProfile : Serializable {

    @SerializedName("Regdate")
    @Expose
    var registrationDate: Date? = null
    @SerializedName("Exp")
    @Expose
    var exp: Experience = Experience()
    @SerializedName("Stats")
    @Expose
    var stats: Statistics = Statistics()
    @SerializedName("Achievements")
    @Expose
    var achievements: List<Achievement>? = null
}
