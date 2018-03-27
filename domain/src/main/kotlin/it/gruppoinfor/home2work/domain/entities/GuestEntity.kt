package it.gruppoinfor.home2work.domain.entities

data class GuestEntity(
        val id: Long,
        val shareId: Long,
        val user: UserEntity,
        val startLat: Double,
        val startLng: Double,
        val endLat: Double?,
        val endLng: Double?,
        val status: GuestStatusEntity,
        val distance: Int
)
