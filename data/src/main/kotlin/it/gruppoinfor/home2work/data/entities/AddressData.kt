package it.gruppoinfor.home2work.data.entities


import com.google.gson.annotations.SerializedName

data class AddressData(
        @SerializedName("Latitude") var latitude: Double,
        @SerializedName("Longitude") var longitude: Double,
        @SerializedName("City") var city: String,
        @SerializedName("District") var district: String? = null,
        @SerializedName("Cap") var postalCode: String? = null,
        @SerializedName("Street") var street: String? = null,
        @SerializedName("Civic") var number: Int = -1
)