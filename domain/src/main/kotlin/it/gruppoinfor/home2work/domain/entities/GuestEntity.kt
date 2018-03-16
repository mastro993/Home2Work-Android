package it.gruppoinfor.home2work.domain.entities

data class GuestEntity(
        val id: Long,
        val shareId: Long,
        val user: UserEntity,
        val startLocation: LatLngEntity,
        val endLocation: LatLngEntity,
        val status: Status,
        val distance: Int
) {
    enum class Status constructor(val value: Int) {
        JOINED(0),
        COMPLETED(1),
        CANCELED(2)
    }
}
