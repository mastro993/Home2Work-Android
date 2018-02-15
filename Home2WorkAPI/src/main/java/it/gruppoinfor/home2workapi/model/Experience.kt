package it.gruppoinfor.home2workapi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Experience {
    @SerializedName("Amount")
    @Expose
    var value: Int = 0
    @SerializedName("Level")
    @Expose
    var level: Int = 0
    @SerializedName("Progress")
    @Expose
    var progress: Float = 0f
    @SerializedName("NextLvlExp")
    @Expose
    var nextLvlExp: Int = 0

    val expForNextLevel
        get() = nextLvlExp - value
}
