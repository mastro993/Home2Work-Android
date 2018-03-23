package it.gruppoinfor.home2work.domain.entities


data class AddressEntity(
        val latitude: Double,
        val longitude: Double,
        val city: String,
        val district: String?,
        val postalCode: String?,
        val street: String?,
        val number: Int
)