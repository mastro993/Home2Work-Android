package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName

data class ExperienceData(
        @SerializedName("Level") var level: Int = -1,
        @SerializedName("Amount") var amount: Int = -1,
        @SerializedName("CurrentLvLExp") var currentLvLExp: Int = -1,
        @SerializedName("NextLvlExp") var nextLvlExp: Int = -1,
        @SerializedName("MonthExp") var monthExp: Int = -1
)