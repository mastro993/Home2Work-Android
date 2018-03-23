package it.gruppoinfor.home2work.entities

import java.util.*
import kotlin.collections.ArrayList

data class Share(
        var id: Long = -1,
        var host: User? = null,
        var status: ShareStatus? = null,
        var date: Date? = null,
        var type: ShareType? = null,
        var guests: ArrayList<Guest> = ArrayList()
)
