package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.ProfileData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ProfileEntity
import javax.inject.Inject
import javax.inject.Singleton



class ProfileEntityDataMapper @Inject constructor() : Mapper<ProfileEntity, ProfileData>() {

    override fun mapFrom(from: ProfileEntity): ProfileData {

        val exp = ExperienceEntityDataMapper().mapFrom(from.exp)
        val stats = StatisticsEntityDataMapper().mapFrom(from.stats)

        val activity = from.activity.mapValues {
            SharingActivityEntityDataMapper().mapFrom(it.value)
        }

        return ProfileData(
                exp = exp,
                stats = stats,
                activity = activity,
                regdate = from.regdate
        )
    }
}