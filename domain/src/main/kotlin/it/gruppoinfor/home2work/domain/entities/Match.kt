package it.gruppoinfor.home2work.domain.entities

import java.util.*

data class Match(

        var matchId: Long,
        var host: User,
        var homeScore: Int,
        var jobScore: Int,
        var timeScore: Int,
        var arrivalTime: Date,
        var departureTime: Date,
        var distance: Int,
        var isNew: Boolean,
        var isHidden: Boolean

)
