package it.gruppoinfor.home2work.common.mappers

import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.UserRankingEntity
import it.gruppoinfor.home2work.entities.UserRanking
import javax.inject.Inject


class UserRankingEntityUserRankingMapper @Inject constructor() : Mapper<UserRankingEntity, UserRanking>() {

    override fun mapFrom(from: UserRankingEntity): UserRanking {

        return UserRanking(
                position = from.position,
                userId = from.userId,
                userName = from.userName,
                companyId = from.companyId,
                companyName = from.companyName,
                amount = from.amount
        )

    }
}