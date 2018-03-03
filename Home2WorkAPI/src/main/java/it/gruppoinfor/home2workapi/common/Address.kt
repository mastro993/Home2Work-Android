package it.gruppoinfor.home2workapi.common


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

class Address : Serializable {

    @SerializedName("City")
    @Expose
    var city: String = ""
    @SerializedName("Cap")
    @Expose
    var postalCode: String = ""
    @SerializedName("AddressLine")
    @Expose
    var address: String = ""

    override fun toString(): String {
        return "${this.address}, ${this.postalCode}, ${this.city}"
    }

}