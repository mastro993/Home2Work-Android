package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.CompanyData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.CompanyEntity
import javax.inject.Inject
import javax.inject.Singleton



class CompanyEntityDataMapper @Inject constructor() : Mapper<CompanyEntity, CompanyData>() {
    override fun mapFrom(from: CompanyEntity): CompanyData {
        val addressData = AddressEntityDataMapper().mapFrom(from.address)
        return CompanyData(
                id = from.id,
                name = from.name,
                address = addressData,
                domain = from.domain

        )
    }

}
