package it.gruppoinfor.home2workapi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

class Company : Serializable {

    @SerializedName("Id")
    @Expose
    var id: Long = 0
    @SerializedName("Name")
    @Expose
    var name: String = ""
    @SerializedName("LatLng")
    @Expose
    var location: LatLng = LatLng()
    @SerializedName("Address")
    @Expose
    var address: Address = Address()

    override fun toString(): String {
        return "$name (${address.city})"
    }
}