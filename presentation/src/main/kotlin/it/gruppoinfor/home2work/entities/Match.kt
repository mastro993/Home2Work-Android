package it.gruppoinfor.home2work.entities

import java.util.*

data class Match(
        var id: Long,
        var host: User,
        var homeScore: Int? = null,
        var jobScore: Int? = null,
        var timeScore: Int? = null,
        var arrivalTime: Date? = null,
        var departureTime: Date? = null,
        var distance: Int?,
        var isNew: Boolean = true,
        var isHidden: Boolean = true
)
