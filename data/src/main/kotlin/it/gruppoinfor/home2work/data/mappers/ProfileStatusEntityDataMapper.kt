package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.ProfileStatusData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ProfileStatusEntity
import javax.inject.Inject

class ProfileStatusEntityDataMapper @Inject constructor() : Mapper<ProfileStatusEntity, ProfileStatusData>() {
    override fun mapFrom(from: ProfileStatusEntity): ProfileStatusData {
        return ProfileStatusData(from.status, from.date)
    }
}