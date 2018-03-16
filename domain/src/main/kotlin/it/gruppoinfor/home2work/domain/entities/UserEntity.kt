package it.gruppoinfor.home2work.domain.entities

import java.util.*

data class UserEntity(
        val id: Long,
        val email: String,
        val name: String,
        val surname: String,
        val homeLatLng: LatLngEntity?,
        val address: AddressEntity?,
        val company: CompanyEntity?,
        val regdate: Date,
        val configured: Boolean
)
