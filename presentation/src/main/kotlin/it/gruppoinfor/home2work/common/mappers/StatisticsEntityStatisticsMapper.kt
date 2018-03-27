package it.gruppoinfor.home2work.common.mappers

import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.StatisticsEntity
import it.gruppoinfor.home2work.entities.Statistics
import javax.inject.Inject


class StatisticsEntityStatisticsMapper @Inject constructor() : Mapper<StatisticsEntity, Statistics>() {
    override fun mapFrom(from: StatisticsEntity): Statistics {
        return Statistics(
                totalShares = from.totalShares,
                totalGuestShares = from.totalGuestShares,
                totalHostShares = from.totalHostShares,
                monthShares = from.monthShares,
                monthlySharesAvg = from.monthlySharesAvg,
                sharedDistance = from.sharedDistance,
                monthSharedDistance = from.monthSharedDistance,
                monthSharedDistanceAvg = from.monthSharedDistanceAvg,
                bestMonthShares = from.bestMonthShares,
                longestShare = from.longestShare
        )
    }
}