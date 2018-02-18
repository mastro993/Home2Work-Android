package it.gruppoinfor.home2workapi.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

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
    @SerializedName("Activity")
    @Expose
    var activity: List<MonthActivity> = ArrayList<MonthActivity>()
}
