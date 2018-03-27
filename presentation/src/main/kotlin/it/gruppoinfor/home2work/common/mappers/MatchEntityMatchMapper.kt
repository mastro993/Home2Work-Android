package it.gruppoinfor.home2work.common.mappers

import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.MatchEntity
import it.gruppoinfor.home2work.entities.Match
import javax.inject.Inject


class MatchEntityMatchMapper @Inject constructor() : Mapper<MatchEntity, Match>() {

    override fun mapFrom(from: MatchEntity): Match {

        val host = UserEntityUserMapper().mapFrom(from.host)

        return Match(
                id = from.id,
                host = host,
                homeScore = from.homeScore,
                jobScore = from.jobScore,
                timeScore = from.timeScore,
                arrivalTime = from.arrivalTime,
                departureTime = from.departureTime,
                distance = from.distance,
                isNew = from.isNew,
                isHidden = from.isHidden
        )
    }
}