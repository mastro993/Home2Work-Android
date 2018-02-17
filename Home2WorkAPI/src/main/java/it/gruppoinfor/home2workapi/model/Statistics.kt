package it.gruppoinfor.home2workapi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Statistics : Serializable {


    @SerializedName("Shares")
    @Expose
    var shares: Int = 0
    @SerializedName("GuestShares")
    @Expose
    var guestShares: Int = 0
    @SerializedName("HostShares")
    @Expose
    var hostShares: Int = 0
    @SerializedName("MonthShares")
    @Expose
    var monthShares: Int = 0
    @SerializedName("MonthSharesAvg")
    @Expose
    var monthSharesAvg: Float = 0f
    @SerializedName("SharedDistance")
    @Expose
    var sharedDistance: Int = 0
    @SerializedName("MonthSharedDistance")
    @Expose
    var monthSharedDistance: Int = 0
    @SerializedName("MonthSharedDistanceAvg")
    @Expose
    var monthSharedDistanceAvg: Float = 0f
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

