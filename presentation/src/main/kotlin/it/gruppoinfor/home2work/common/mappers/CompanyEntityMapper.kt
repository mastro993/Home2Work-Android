package it.gruppoinfor.home2work.common.mappers

import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.CompanyEntity
import it.gruppoinfor.home2work.entities.Company
import javax.inject.Inject


class CompanyEntityMapper @Inject constructor() : Mapper<Company, CompanyEntity>() {
    override fun mapFrom(from: Company): CompanyEntity {

        val address = AddressAddressEntityMapper().mapFrom(from.address)

        return CompanyEntity(
                id = from.id,
                name = from.name,
                address = address,
                domain = from.domain
        )
    }
}