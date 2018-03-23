package it.gruppoinfor.home2work.entities

data class Company(
        var id: Long = -1,
        var name: String,
        var formattedName: String,
        var address: Address,
        var domain: String
)