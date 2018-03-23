package it.gruppoinfor.home2work.entities

data class Guest(
        var id: Long = -1,
        var shareId: Long = -1,
        var user: User? = null,
        var startLat: Double,
        var startLng: Double,
        var endLat: Double?,
        var endLng: Double?,
        var status: ShareStatus? = null,
        var distance: Int = -1
)
