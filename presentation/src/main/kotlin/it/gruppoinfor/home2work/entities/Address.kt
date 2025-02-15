package it.gruppoinfor.home2work.entities

import java.io.Serializable


data class Address(
        var latitude: Double,
        var longitude: Double,
        var region: String,
        var district: String?,
        var postalCode: String?,
        var city: String,
        var street: String?
) : Serializable