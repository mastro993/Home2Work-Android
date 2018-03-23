package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.AddressData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.AddressEntity
import javax.inject.Inject
import javax.inject.Singleton



class AddressEntityDataMapper @Inject constructor() : Mapper<AddressEntity, AddressData>() {

    override fun mapFrom(from: AddressEntity): AddressData {
        return AddressData(
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