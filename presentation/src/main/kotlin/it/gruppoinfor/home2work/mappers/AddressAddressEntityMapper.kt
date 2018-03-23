package it.gruppoinfor.home2work.mappers


import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.AddressEntity
import it.gruppoinfor.home2work.entities.Address
import javax.inject.Inject
import javax.inject.Singleton


class AddressAddressEntityMapper @Inject constructor() : Mapper<Address, AddressEntity>() {
    override fun mapFrom(from: Address): AddressEntity {
        return AddressEntity(
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