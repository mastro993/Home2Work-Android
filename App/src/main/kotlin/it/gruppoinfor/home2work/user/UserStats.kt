package it.gruppoinfor.home2work.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserStats : Serializable {


    @SerializedName("TotalShares")
    @Expose
    var totalShares: Int = 0
    @SerializedName("TotalGuestShares")
    @Expose
    var totalGuestShares: Int = 0
    @SerializedName("TotalHostShares")
    @Expose
    var totalHostShares: Int = 0
    @SerializedName("MonthShares")
    @Expose
    var monthShares: Int = 0
    @SerializedName("MonthlySharesAvg")
    @Expose
    var monthlySharesAvg: Float = 0f
    @SerializedName("TotalSharedDistance")
    @Expose
    var sharedDistance: Int = 0
    @SerializedName("MonthSharedDistance")
    @Expose
    var monthSharedDistance: Int = 0
    @SerializedName("MonthlySharedDistanceAvg")
    @Expose
    var monthSharedDistanceAvg: Float = 0f
    @SerializedName("BestMonthShares")
    @Expose
    var bestMonthShares: Int = 0
    @SerializedName("LongestShare")
    @Expose
    var longestShare: Float = 0f


    companion object {

        val KILOMETER_PER_LITRE = 7.5
        val EMISSIONS_PER_LITER = 9.0
    }
}

