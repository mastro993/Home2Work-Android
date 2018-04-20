package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.ProfileData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ProfileEntity
import javax.inject.Inject


class ProfileDataEntityMapper @Inject constructor() : Mapper<ProfileData, ProfileEntity>() {

    override fun mapFrom(from: ProfileData): ProfileEntity {

        val karma = KarmaDataEntityMapper().mapFrom(from.karma)
        val stats = StatisticsDataEntityMapper().mapFrom(from.stats)

        val activity = from.activity.mapValues {
            SharingActivityDataEntityMapper().mapFrom(it.value)
        }

        val status = from.status?.let {
            ProfileStatusDataEntityMapper().mapFrom(it)
        }

        return ProfileEntity(
                status = status,
                karma = karma,
                stats = stats,
                activity = activity,
                regdate = from.regdate
        )
    }
}