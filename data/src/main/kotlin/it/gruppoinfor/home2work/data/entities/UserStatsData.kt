package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName

data class UserStatsData(
        @SerializedName("TotalShares") var totalShares: Int = -1,
        @SerializedName("TotalGuestShares") var totalGuestShares: Int = -1,
        @SerializedName("TotalHostShares") var totalHostShares: Int = -1,
        @SerializedName("MonthShares") var monthShares: Int = -1,
        @SerializedName("MonthlySharesAvg") var monthlySharesAvg: Float = -1f,
        @SerializedName("TotalSharedDistance") var sharedDistance: Int = -1,
        @SerializedName("MonthSharedDistance") var monthSharedDistance: Int = -1,
        @SerializedName("MonthlySharedDistanceAvg") var monthSharedDistanceAvg: Float = -1f,
        @SerializedName("BestMonthShares") var bestMonthShares: Int = -1,
        @SerializedName("LongestShare") var longestShare: Float = -1f
)

