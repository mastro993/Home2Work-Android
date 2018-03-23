package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.SharingActivityData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.SharingActivityEntity
import javax.inject.Inject
import javax.inject.Singleton



class SharingActivityDataEntityMapper @Inject constructor() : Mapper<SharingActivityData, SharingActivityEntity>() {

    override fun mapFrom(from: SharingActivityData): SharingActivityEntity {
        return SharingActivityEntity(
                shares = from.shares,
                distance = from.distance
        )
    }
}