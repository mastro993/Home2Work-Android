package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName

data class KarmaData(
        @SerializedName("Level") var level: Int = -1,
        @SerializedName("Amount") var amount: Int = -1,
        @SerializedName("CurrentLvLKarma") var currentLvLKarma: Int = -1,
        @SerializedName("NextLvlKarma") var nextLvlKarma: Int = -1,
        @SerializedName("MonthKarma") var monthKarma: Int = -1
)