package it.gruppoinfor.home2work.domain.entities

import java.util.*


data class User(
        val id: Long,
        val email: String,
        val name: String,
        val surname: String,
        val homeLatLng: LatLng,
        val address: Address,
        val company: Company,
        val regdate: Date
) {
    val formattedName: String = "$name $surname"
}



