package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

@Entity
data class UserLocationData(
        @Id var id: Long? = null,
        @SerializedName("UserId") var userId: Long? = null,
        @SerializedName("Latitude") var latitude: Double = 0.0,
        @SerializedName("Longitude") var longitude: Double = 0.0,
        @SerializedName("Date") var date: Date? = null
)