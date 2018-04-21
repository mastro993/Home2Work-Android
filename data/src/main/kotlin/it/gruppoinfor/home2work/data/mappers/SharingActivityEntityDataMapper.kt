package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.SharingActivityData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.SharingActivityEntity
import javax.inject.Inject
import javax.inject.Singleton



class SharingActivityEntityDataMapper @Inject constructor() : Mapper<SharingActivityEntity,SharingActivityData>() {

    override fun mapFrom(from: SharingActivityEntity): SharingActivityData {
        return SharingActivityData(
                shares = from.shares,
                sharesTrend = from.sharesTrend,
                distance = from.distance,
                distanceTrend = from.distanceTrend
        )
    }
}