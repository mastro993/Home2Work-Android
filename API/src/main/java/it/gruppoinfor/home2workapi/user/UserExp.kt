package it.gruppoinfor.home2workapi.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserExp {
    @SerializedName("Level")
    @Expose
    var level: Int = 0
    @SerializedName("Amount")
    @Expose
    var amount: Int = 0
    @SerializedName("CurrentLvLExp")
    @Expose
    var currentLvLExp: Int = 0
    @SerializedName("NextLvlExp")
    @Expose
    var nextLvlExp: Int = 0

    val expForNextLevel
        get() = nextLvlExp - amount

    var progress: Float = 0f
        get() {
            val toNextLevelExp = nextLvlExp - currentLvLExp
            val expDelta = amount - currentLvLExp
            return if (toNextLevelExp == 0) 0f
            else (100f / toNextLevelExp) * expDelta
        }
}
