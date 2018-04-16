package it.gruppoinfor.home2work.entities

data class UserRanking(
        var position: Int,
        var userId: Long,
        var userName: String,
        var companyId: Long,
        var companyName: String,
        var amount: Int
)