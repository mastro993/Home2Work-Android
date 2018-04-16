package it.gruppoinfor.home2work.common.mappers

import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.UserRankingEntity
import it.gruppoinfor.home2work.entities.UserRanking
import javax.inject.Inject


class UserRankingUserRankingEntityMapper @Inject constructor() : Mapper<UserRanking, UserRankingEntity>() {

    override fun mapFrom(from: UserRanking): UserRankingEntity {

        return UserRankingEntity(
                position = from.position,
                userId = from.userId,
                userName = from.userName,
                companyId = from.companyId,
                companyName = from.companyName,
                amount = from.amount
        )

    }
}