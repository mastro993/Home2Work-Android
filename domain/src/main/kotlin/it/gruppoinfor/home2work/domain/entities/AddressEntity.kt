package it.gruppoinfor.home2work.domain.entities


data class AddressEntity(
        val city: String,
        val district: String,
        val postalCode: String,
        val street: String,
        val number: Int
)