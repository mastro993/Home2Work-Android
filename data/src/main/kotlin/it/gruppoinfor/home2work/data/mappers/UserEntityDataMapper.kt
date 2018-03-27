package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.UserData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.UserEntity
import javax.inject.Inject


class UserEntityDataMapper @Inject constructor() : Mapper<UserEntity, UserData>() {

    override fun mapFrom(from: UserEntity): UserData {

        val addressData = from.address?.let {
            AddressEntityDataMapper().mapFrom(it)
        }

        val companyData = from.company?.let {
            CompanyEntityDataMapper().mapFrom(it)
        }

        return UserData(
                id = from.id,
                email = from.email,
                name = from.name,
                surname = from.surname,
                address = addressData,
                company = companyData,
                regdate = from.regdate,
                accessToken = from.accessToken
        )
    }


}