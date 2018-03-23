package it.gruppoinfor.home2work.data.entities


import com.google.gson.annotations.SerializedName
import java.util.*


data class UserData(
        @SerializedName("Id") var id: Long = -1,
        @SerializedName("Email") var email: String,
        @SerializedName("Name") var name: String,
        @SerializedName("Surname") var surname: String,
        @SerializedName("Address") var address: AddressData? = null,
        @SerializedName("Company") var company: CompanyData,
        @SerializedName("Regdate") var regdate: Date,
        @SerializedName("AccessToken") var accessToken: String?
)
