package it.gruppoinfor.home2workapi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

class Rank : Serializable {
    @SerializedName("Shares")
    @Expose
    var shares: Int = 0
    @SerializedName("MonthShares")
    @Expose
    var monthShares: Int = 0
    @SerializedName("LastMonthShares")
    @Expose
    var lastMonthShares: Int = 0
    @SerializedName("SharedDistance")
    @Expose
    var sharedDistance: Int = 0
    @SerializedName("MonthSharedDistance")
    @Expose
    var monthSharedDistance: Int = 0
    @SerializedName("LastMonthSharedDistance")
    @Expose
    var lastMonthSharedDistance: Int = 0
}
