package it.gruppoinfor.home2work.domain.entities

import java.util.*

data class UserLocationEntity(
        val userId: Long,
        val latitude: Double,
        val langitude: Double,
        val date: Date
)