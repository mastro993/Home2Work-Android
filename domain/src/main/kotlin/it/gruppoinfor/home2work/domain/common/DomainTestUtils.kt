package it.gruppoinfor.home2work.domain.common

import it.gruppoinfor.home2work.domain.entities.Address
import it.gruppoinfor.home2work.domain.entities.Company
import it.gruppoinfor.home2work.domain.entities.LatLng
import it.gruppoinfor.home2work.domain.entities.User
import java.util.*


class DomainTestUtils {

    companion object {

        fun getTestAddress(seed: Any? = null): Address {
            return Address(
                    city = "Citta' $seed",
                    postalCode = "00000",
                    address = "Indirizzo $seed"

            )
        }

        fun getTestLocation(): LatLng {
            return LatLng(0.0, 0.0)
        }


        fun getTestCompany(id: Long): Company {
            return Company(
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

        fun generateCompanyList(): List<Company> {
            return (0L..4L).map { getTestCompany(it) }
        }
    }
}