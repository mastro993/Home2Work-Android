package it.gruppoinfor.home2work.domain.entities

import java.util.*

data class MatchEntity(
        var id: Long,
        var host: UserEntity,
        var homeScore: Int?,
        var jobScore: Int?,
        var timeScore: Int?,
        var arrivalTime: Date?,
        var departureTime: Date?,
        var distance: Int?,
        var isNew: Boolean,
        var isHidden: Boolean
)
