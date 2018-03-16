package it.gruppoinfor.home2work.domain.entities

data class RankingsEntity(
        val shares: Int,
        val monthShares: Int,
        val monthSharesAvg: Int,
        val sharedDistance: Int,
        val monthSharedDistance: Int,
        val monthSharedDistanceAvg: Int
)
