package it.gruppoinfor.home2work.mappers

import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.SharingActivityEntity
import it.gruppoinfor.home2work.entities.SharingActivity
import javax.inject.Inject


class SharingActivityEntitySharingActivityMapper @Inject constructor() : Mapper<SharingActivityEntity, SharingActivity>() {
    override fun mapFrom(from: SharingActivityEntity): SharingActivity {
        return SharingActivity(
                shares = from.shares,
                distance = from.distance
        )
    }
}