package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.UserData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.UserEntity
import javax.inject.Inject


class UserDataEntityMapper @Inject constructor() : Mapper<UserData, UserEntity>() {

    override fun mapFrom(from: UserData): UserEntity {

        val addressEntity = from.address?.let {
            AddressDataEntityMapper().mapFrom(it)
        }

        val companyEntity = from.company?.let {
            CompanyDataEntityMapper().mapFrom(it)
        }

        return UserEntity(
                id = from.id,
                email = from.email,
                name = from.name,
                surname = from.surname,
                address = addressEntity,
                company = companyEntity,
                regdate = from.regdate,
                accessToken = from.accessToken
        )
    }
}