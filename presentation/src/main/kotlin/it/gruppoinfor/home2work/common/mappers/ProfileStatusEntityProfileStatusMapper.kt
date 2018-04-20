package it.gruppoinfor.home2work.common.mappers

import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ProfileStatusEntity
import it.gruppoinfor.home2work.entities.ProfileStatus
import javax.inject.Inject

class ProfileStatusEntityProfileStatusMapper @Inject constructor() : Mapper<ProfileStatusEntity, ProfileStatus>() {
    override fun mapFrom(from: ProfileStatusEntity): ProfileStatus {
        return ProfileStatus(from.status, from.date)
    }
}