package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.CompanyData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.CompanyEntity
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CompanyDataEntityMapper @Inject constructor() : Mapper<CompanyData, CompanyEntity>() {
    override fun mapFrom(from: CompanyData): CompanyEntity {
        val latLngEntity = LatLngDataEntityMapper().mapFrom(from.location)
        val addressEntity = AddressDataEntityMapper().mapFrom(from.address)
        return CompanyEntity(
                id = from.id,
                name = from.name,
                location = latLngEntity,
                address = addressEntity
        )
    }
}
