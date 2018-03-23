package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.StatisticsData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.StatisticsEntity
import javax.inject.Inject
import javax.inject.Singleton



class StatisticsDataEntityMapper @Inject constructor() : Mapper<StatisticsData, StatisticsEntity>() {

    override fun mapFrom(from: StatisticsData): StatisticsEntity {
        return StatisticsEntity(
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