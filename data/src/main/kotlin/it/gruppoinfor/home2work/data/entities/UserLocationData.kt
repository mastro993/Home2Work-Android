package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

@Entity
data class UserLocationData(
        @Id @SerializedName("Id") val id: Long? = null,
        @SerializedName("UserId") val userId: Long? = null,
        @SerializedName("Latitude") val latitude: Double = 0.0,
        @SerializedName("Longitude") val longitude: Double = 0.0,
        @SerializedName("Date") val date: Date? = null
)