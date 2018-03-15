package it.gruppoinfor.home2workapi.user


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class UserProfile : Serializable {


    @SerializedName("Exp")
    @Expose
    var exp: UserExp = UserExp()
    @SerializedName("Stats")
    @Expose
    var stats: UserStats = UserStats()
    @SerializedName("Activity")
    @Expose
    var activity: List<MonthActivity> = ArrayList()
    @SerializedName("Regdate")
    @Expose
    var regdate: Date? = null
}
