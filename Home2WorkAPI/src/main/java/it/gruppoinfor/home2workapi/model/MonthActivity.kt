package it.gruppoinfor.home2workapi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class MonthActivity {
    @SerializedName("Year")
    @Expose
    var year: Int = 0
    @SerializedName("Month")
    @Expose
    var month: Int = 0
    @SerializedName("Shares")
    @Expose
    var shares: Int = 0
    @SerializedName("Distance")
    @Expose
    var distance: Int = 0
}