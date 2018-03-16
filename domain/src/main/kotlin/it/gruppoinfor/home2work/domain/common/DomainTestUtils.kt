package it.gruppoinfor.home2work.domain.common

import it.gruppoinfor.home2work.domain.entities.AddressEntity
import it.gruppoinfor.home2work.domain.entities.CompanyEntity
import it.gruppoinfor.home2work.domain.entities.LatLngEntity
import java.util.*


class DomainTestUtils {

    companion object {

        fun getTestAddress(seed: Any? = null): AddressEntity {
            return AddressEntity(
                    city = "Citta' $seed",
                    postalCode = "00000",
                    address = "Indirizzo $seed"

            )
        }

        fun getTestLocation(): LatLngEntity {
            return LatLngEntity(0.0, 0.0)
        }


        fun getTestCompany(id: Long): CompanyEntity {
            return CompanyEntity(
                    id = id,
                    name = "Azienda$id",
                    location = getTestLocation(),
                    address = getTestAddress(id)
            )
        }

        fun getTestUser(id: Long): User {
            return User(
                    id = id,
                    email = "utente$id@email.com",
                    name = "Utente $id",
                    surname = "Utente $id",
                    homeLatLng = getTestLocation(),
                    address = getTestAddress(id),
                    company = getTestCompany(id),
                    regdate = Date()
            )
        }

        fun generateUserList(): List<User> {
            return (0L..4L).map { getTestUser(it) }
        }

        fun generateCompanyList(): List<CompanyEntity> {
            return (0L..4L).map { getTestCompany(it) }
        }
    }
}