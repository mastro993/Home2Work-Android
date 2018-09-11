package it.gruppoinfor.home2work.data.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "user_location")
data class UserLocationData(
        @PrimaryKey var id: Long? = null,
        @SerializedName("UserId") var userId: Long,
        @SerializedName("Latitude") var latitude: Double = 0.0,
        @SerializedName("Longitude") var longitude: Double = 0.0,
        @SerializedName("Date") var date: Date,
        @SerializedName("Type") var type: Int?
)