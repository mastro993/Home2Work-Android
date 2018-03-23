package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.MatchData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.MatchEntity
import javax.inject.Inject

class MatchEntityDataMapper @Inject constructor() : Mapper<MatchEntity, MatchData>() {
    override fun mapFrom(from: MatchEntity): MatchData {

        val host = UserEntityDataMapper().mapFrom(from.host)

        return MatchData(
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