package it.gruppoinfor.home2work.mappers

import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.CompanyEntity
import it.gruppoinfor.home2work.entities.Company
import javax.inject.Inject


class CompanyEntityCompanyMapper @Inject constructor() : Mapper<CompanyEntity, Company>() {
    override fun mapFrom(from: CompanyEntity): Company {

        val address = AddressEntityAddressMapper().mapFrom(from.address)

        return Company(
                id = from.id,
                name = from.name,
                formattedName = "${from.name} (${from.address.city})",
                address = address,
                domain = from.domain
        )
    }
}