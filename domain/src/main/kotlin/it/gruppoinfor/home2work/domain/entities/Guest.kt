package it.gruppoinfor.home2work.domain.entities

data class Guest(
        val shareId: Long,
        val user: User,
        val startLocation: LatLng,
        val endLocation: LatLng,
        val status: Status,
        val distance: Int
) {
    enum class Status constructor(val value: Int) {
        JOINED(0),
        COMPLETED(1),
        CANCELED(2)
    }
}
