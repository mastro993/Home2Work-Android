package it.gruppoinfor.home2work.entities

data class UserRanking(
        var position: Int,
        var userId: Long,
        val avatarUrl: String,
        var userName: String,
        var companyId: Long,
        var companyName: String,
        var amount: Int
)