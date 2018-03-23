package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.MatchData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.MatchEntity
import javax.inject.Inject

class MatchDataEntityMapper @Inject constructor() : Mapper<MatchData, MatchEntity>() {
    override fun mapFrom(from: MatchData): MatchEntity {

        val host = UserDataEntityMapper().mapFrom(from.host)

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