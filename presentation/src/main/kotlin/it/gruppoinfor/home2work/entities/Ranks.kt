package it.gruppoinfor.home2work.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

class Ranks(
        var shares: Int,
        var monthShares: Int,
        var monthSharesAvg: Int,
        var sharedDistance: Int,
        var monthSharedDistance: Int,
        var monthSharedDistanceAvg: Int
)
