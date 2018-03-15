package it.gruppoinfor.home2work.domain.entities

data class UserStats(
        val totalShares: Int,
        val totalGuestShares: Int,
        val totalHostShares: Int,
        val monthShares: Int,
        val monthlySharesAvg: Float,
        val sharedDistance: Int,
        val monthSharedDistance: Int,
        val monthSharedDistanceAvg: Float,
        val bestMonthShares: Int,
        val longestShare: Float
)

