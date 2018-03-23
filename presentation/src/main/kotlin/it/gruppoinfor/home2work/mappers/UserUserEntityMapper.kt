package it.gruppoinfor.home2work.mappers

import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.UserEntity
import it.gruppoinfor.home2work.entities.User
import javax.inject.Inject
import javax.inject.Singleton



class UserUserEntityMapper @Inject constructor() : Mapper<User, UserEntity>() {

    override fun mapFrom(from: User): UserEntity {

        val address = from.address?.let {
            AddressAddressEntityMapper().mapFrom(it)
        }
        val company = from.company?.let {
            CompanyEntityMapper().mapFrom(it)
        }

        return UserEntity(
                id = from.id,
                email = from.email,
                name = from.name,
                surname = from.surname,
                address = address,
                company = company,
                regdate = from.regdate,
                accessToken = null
        )

    }
}