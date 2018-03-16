package it.gruppoinfor.home2work.domain.entities

data class CompanyEntity(
        val id: Long,
        val name: String,
        val location: LatLngEntity,
        val address: AddressEntity
)