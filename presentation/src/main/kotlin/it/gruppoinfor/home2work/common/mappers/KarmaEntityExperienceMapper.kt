package it.gruppoinfor.home2work.common.mappers

import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.KarmaEntity
import it.gruppoinfor.home2work.entities.Karma
import javax.inject.Inject


class KarmaEntityExperienceMapper @Inject constructor() : Mapper<KarmaEntity, Karma>() {
    override fun mapFrom(from: KarmaEntity): Karma {
        return Karma(
                level = from.level,
                amount = from.amount,
                currentLvlKarma = from.currentLvLKarma,
                nextLvlKarma = from.nextLvlKarma,
                monthKarma = from.monthKarma
        )
    }
}