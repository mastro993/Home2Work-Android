package it.gruppoinfor.home2workapi.company

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import it.gruppoinfor.home2workapi.FullAddress
import it.gruppoinfor.home2workapi.LatLng

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
    var address: FullAddress = FullAddress()

    override fun toString(): String {
        return "$name (${address.city})"
    }
}