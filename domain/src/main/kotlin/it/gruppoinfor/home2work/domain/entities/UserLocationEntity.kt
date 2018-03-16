package it.gruppoinfor.home2work.domain.entities

import java.util.*

data class UserLocationEntity(
        val id: Long,
        val userId: Long,
        val latLng: LatLngEntity,
        val date: Date
)