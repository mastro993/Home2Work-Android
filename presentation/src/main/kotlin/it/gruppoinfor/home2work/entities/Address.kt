package it.gruppoinfor.home2work.entities


data class Address(
        var latitude: Double,
        var longitude: Double,
        var city: String,
        var district: String?,
        var postalCode: String?,
        var street: String?,
        var number: Int = -1
)