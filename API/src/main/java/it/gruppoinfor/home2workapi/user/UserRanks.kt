package it.gruppoinfor.home2workapi.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

class UserRanks : Serializable {
    @SerializedName("Shares")
    @Expose
    var shares: Int = 0
    @SerializedName("MonthShares")
    @Expose
    var monthShares: Int = 0
    @SerializedName("MonthSharesAvg")
    @Expose
    var monthSharesAvg: Int = 0
    @SerializedName("SharedDistance")
    @Expose
    var sharedDistance: Int = 0
    @SerializedName("MonthSharedDistance")
    @Expose
    var monthSharedDistance: Int = 0
    @SerializedName("MonthSharedDistanceAvg")
    @Expose
    var monthSharedDistanceAvg: Int = 0
}
