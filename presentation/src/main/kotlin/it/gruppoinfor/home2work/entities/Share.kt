package it.gruppoinfor.home2work.entities

import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class Share(
        var id: Long = -1,
        var host: User,
        var status: ShareStatus? = null,
        var date: Date? = null,
        var type: ShareType? = null,
        var guests: ArrayList<Guest> = ArrayList(),
        val startLat: Double,
        val startLng: Double,
        val endLat: Double?,
        val endLng: Double?,
        var sharedDistance: Int = 0
) : Serializable
