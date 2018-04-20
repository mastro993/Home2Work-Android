package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.ProfileStatusData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ProfileStatusEntity
import javax.inject.Inject

class ProfileStatusDataEntityMapper @Inject constructor() : Mapper<ProfileStatusData, ProfileStatusEntity>() {
    override fun mapFrom(from: ProfileStatusData): ProfileStatusEntity {
        return ProfileStatusEntity(from.status, from.date)
    }
}