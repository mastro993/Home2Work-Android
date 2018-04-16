package it.gruppoinfor.home2work.domain.entities

data class UserRankingEntity(
        val position: Int,
        val userId: Long,
        val userName: String,
        val companyId: Long,
        val companyName: String,
        val amount: Int
)