package it.gruppoinfor.home2work.mappers

import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ProfileEntity
import it.gruppoinfor.home2work.entities.Profile
import javax.inject.Inject


class ProfileEntityProfileMapper @Inject constructor() : Mapper<ProfileEntity, Profile>() {
    override fun mapFrom(from: ProfileEntity): Profile {
        val exp = ExperienceEntityExperienceMapper().mapFrom(from.exp)
        val activity = from.activity.mapValues { SharingActivityEntitySharingActivityMapper().mapFrom(it.value) }
        val stats = StatisticsEntityStatisticsMapper().mapFrom(from.stats)
        return Profile(
                exp = exp,
                activity = activity,
                regdate = from.regdate,
                stats = stats
        )
    }
}