package it.gruppoinfor.home2work.entities

import java.util.*


data class UserLocation(
        var userId: Long,
        var latitude: Double,
        var longitude: Double,
        var date: Date
)