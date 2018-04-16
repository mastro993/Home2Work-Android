package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName

data class UserRankingData(
        @SerializedName("Position") var position: Int,
        @SerializedName("UserId") var userId: Long,
        @SerializedName("UserName") var userName: String,
        @SerializedName("CompanyId") var companyId: Long,
        @SerializedName("CompanyName") var companyName: String,
        @SerializedName("Amount") var amount: Int
)