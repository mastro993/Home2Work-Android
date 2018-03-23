package it.gruppoinfor.home2work.entities


import java.util.*

data class User(
        var id: Long,
        var avatarUrl: String,
        var email: String,
        var name: String,
        var surname: String,
        var fullName: String,
        var address: Address?,
        var company: Company,
        var regdate: Date
)
