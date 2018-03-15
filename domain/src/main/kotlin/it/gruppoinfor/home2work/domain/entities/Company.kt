package it.gruppoinfor.home2work.domain.entities

data class Company(
        val id: Long,
        val name: String,
        val location: LatLng,
        val address: Address
) {
    val formattedName: String = "$name (${address.city})"
}