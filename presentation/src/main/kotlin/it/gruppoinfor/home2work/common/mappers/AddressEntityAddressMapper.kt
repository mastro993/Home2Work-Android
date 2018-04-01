package it.gruppoinfor.home2work.common.mappers


import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.AddressEntity
import it.gruppoinfor.home2work.entities.Address
import javax.inject.Inject

class AddressEntityAddressMapper @Inject constructor() : Mapper<AddressEntity, Address>() {
    override fun mapFrom(from: AddressEntity): Address {
        return Address(
                latitude = from.latitude,
                longitude = from.longitude,
                city = from.city,
                district = from.district,
                postalCode = from.postalCode,
                street = from.street,
                region = from.region
        )
    }
}