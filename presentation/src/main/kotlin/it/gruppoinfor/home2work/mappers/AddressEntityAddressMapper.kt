package it.gruppoinfor.home2work.mappers


import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.AddressEntity
import it.gruppoinfor.home2work.entities.Address
import javax.inject.Inject
import javax.inject.Singleton

class AddressEntityAddressMapper @Inject constructor() : Mapper<AddressEntity, Address>() {
    override fun mapFrom(from: AddressEntity): Address {
        return Address(
                latitude = from.latitude,
                longitude = from.longitude,
                city = from.city,
                district = from.district,
                postalCode = from.postalCode,
                street = from.street,
                number = from.number
        )
    }
}