package it.gruppoinfor.home2workapi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable
import java.util.Date

class Statistics : Serializable {


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
    @SerializedName("GlobalRanks")
    @Expose
    var globalRanks: Rank = Rank()
    @SerializedName("CompanyRanks")
    @Expose
    var companyRanks: Rank = Rank()

    companion object {

        val KILOMETER_PER_LITRE = 7.5
        val EMISSIONS_PER_LITER = 9.0
    }
}

