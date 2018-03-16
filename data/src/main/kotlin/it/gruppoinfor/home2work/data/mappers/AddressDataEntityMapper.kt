package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.AddressData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.AddressEntity
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AddressDataEntityMapper @Inject constructor() : Mapper<AddressData, AddressEntity>() {
    override fun mapFrom(from: AddressData): AddressEntity {
        return AddressEntity(
                city = from.city,
                district = from.district,
                postalCode = from.postalCode,
                street = from.street,
                number = from.number
        )
    }
}