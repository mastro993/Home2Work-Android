package it.gruppoinfor.home2work.mappers

import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.MatchEntity
import it.gruppoinfor.home2work.entities.Match
import javax.inject.Inject


class MatchMatchEntityMapper @Inject constructor(): Mapper<Match, MatchEntity>() {
    override fun mapFrom(from: Match): MatchEntity {

        val host = UserUserEntityMapper().mapFrom(from.host)

        return MatchEntity(
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