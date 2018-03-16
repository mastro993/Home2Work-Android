package it.gruppoinfor.home2work.data.entities


import com.google.gson.annotations.SerializedName

data class AddressData(
        @SerializedName("City") var city: String,
        @SerializedName("District") var district: String,
        @SerializedName("Cap") var postalCode: String,
        @SerializedName("Street") var street: String? = null,
        @SerializedName("Civic") var number: Int = -1
)