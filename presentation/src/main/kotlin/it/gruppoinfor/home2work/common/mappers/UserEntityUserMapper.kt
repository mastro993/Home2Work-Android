package it.gruppoinfor.home2work.common.mappers

import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.UserEntity
import it.gruppoinfor.home2work.entities.User
import javax.inject.Inject


class UserEntityUserMapper @Inject constructor() : Mapper<UserEntity, User>() {

    companion object {
        const val avatarBaseUrl = "https://hometoworkapi.azurewebsites.net/api/resources/images/avatar/"
    }


    override fun mapFrom(from: UserEntity): User {

        val address = from.address?.let {
            AddressEntityAddressMapper().mapFrom(it)
        }

        val company = from.company?.let {
            CompanyEntityCompanyMapper().mapFrom(it)
        }

        return User(
                id = from.id,
                avatarUrl = "$avatarBaseUrl${from.id}.jpg",
                email = from.email,
                name = from.name,
                surname = from.surname,
                fullName = "${from.name} ${from.surname}",
                address = address,
                company = company,
                regdate = from.regdate
        )

    }
}