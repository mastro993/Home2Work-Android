package it.gruppoinfor.home2work.entities

import java.io.Serializable

data class Guest(
        var id: Long = -1,
        var shareId: Long = -1,
        var user: User,
        var startLat: Double,
        var startLng: Double,
        var endLat: Double?,
        var endLng: Double?,
        var status: GuestStatus? = null,
        var distance: Int = -1
) : Serializable
