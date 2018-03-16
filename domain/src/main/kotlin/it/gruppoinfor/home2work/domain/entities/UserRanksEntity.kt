package it.gruppoinfor.home2work.domain.entities

data class UserRanksEntity(
        val shares: Int,
        val monthShares: Int,
        val monthSharesAvg: Int,
        val sharedDistance: Int,
        val monthSharedDistance: Int,
        val monthSharedDistanceAvg: Int
)
