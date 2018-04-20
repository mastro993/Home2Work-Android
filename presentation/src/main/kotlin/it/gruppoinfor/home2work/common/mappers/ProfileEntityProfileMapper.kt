package it.gruppoinfor.home2work.common.mappers

import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ProfileEntity
import it.gruppoinfor.home2work.entities.Profile
import javax.inject.Inject


class ProfileEntityProfileMapper @Inject constructor() : Mapper<ProfileEntity, Profile>() {
    override fun mapFrom(from: ProfileEntity): Profile {
        val karma = KarmaEntityExperienceMapper().mapFrom(from.karma)
        val activity = from.activity.mapValues { SharingActivityEntitySharingActivityMapper().mapFrom(it.value) }
        val stats = StatisticsEntityStatisticsMapper().mapFrom(from.stats)
        val status = from.status?.let { ProfileStatusEntityProfileStatusMapper().mapFrom(it) }
        return Profile(
                status = status,
                exp = karma,
                activity = activity,
                regdate = from.regdate,
                stats = stats
        )
    }
}