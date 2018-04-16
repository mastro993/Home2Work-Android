package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.UserRankingData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.UserRankingEntity
import javax.inject.Inject


class UserRankingEntityDataMapper @Inject constructor() : Mapper<UserRankingEntity, UserRankingData>() {
    override fun mapFrom(from: UserRankingEntity): UserRankingData {

        return UserRankingData(
                position = from.position,
                userId = from.userId,
                userName = from.userName,
                companyId = from.companyId,
                companyName = from.companyName,
                amount = from.amount
        )

    }
}