package it.gruppoinfor.home2work.domain.entities


data class AddressEntity(
        val latitude: Double,
        val longitude: Double,
        val region: String,
        val district: String?,
        val postalCode: String?,
        val city: String,
        val street: String?
)