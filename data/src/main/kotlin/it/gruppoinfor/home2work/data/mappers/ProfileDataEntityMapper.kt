package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.ProfileData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ProfileEntity
import javax.inject.Inject
import javax.inject.Singleton


class ProfileDataEntityMapper @Inject constructor() : Mapper<ProfileData, ProfileEntity>() {

    override fun mapFrom(from: ProfileData): ProfileEntity {

        val exp = ExperienceDataEntityMapper().mapFrom(from.exp)
        val stats = StatisticsDataEntityMapper().mapFrom(from.stats)

        val activity= from.activity.mapValues {
            SharingActivityDataEntityMapper().mapFrom(it.value)
        }

        return ProfileEntity(
                exp = exp,
                stats = stats,
                activity = activity,
                regdate = from.regdate
        )
    }
}