package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName

data class CompanyData(
        @SerializedName("Id") var id: Long = -1,
        @SerializedName("Name") var name: String,
        @SerializedName("Address") var address: AddressData,
        @SerializedName("Domain") var domain: String
)