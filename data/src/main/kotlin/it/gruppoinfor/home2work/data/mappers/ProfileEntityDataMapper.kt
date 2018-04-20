package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.ProfileData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ProfileEntity
import javax.inject.Inject


class ProfileEntityDataMapper @Inject constructor() : Mapper<ProfileEntity, ProfileData>() {

    override fun mapFrom(from: ProfileEntity): ProfileData {

        val karma = KarmaEntityDataMapper().mapFrom(from.karma)
        val stats = StatisticsEntityDataMapper().mapFrom(from.stats)

        val activity = from.activity.mapValues {
            SharingActivityEntityDataMapper().mapFrom(it.value)
        }

        val status = from.status?.let {
            ProfileStatusEntityDataMapper().mapFrom(it)
        }

        return ProfileData(
                status = status,
                karma = karma,
                stats = stats,
                activity = activity,
                regdate = from.regdate
        )
    }
}