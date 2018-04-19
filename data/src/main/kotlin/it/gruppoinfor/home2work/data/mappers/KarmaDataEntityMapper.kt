package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.KarmaData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.KarmaEntity
import javax.inject.Inject


class KarmaDataEntityMapper @Inject constructor() : Mapper<KarmaData, KarmaEntity>() {

    override fun mapFrom(from: KarmaData): KarmaEntity {
        return KarmaEntity(
                level = from.level,
                amount = from.amount,
                currentLvLKarma = from.currentLvLKarma,
                nextLvlKarma = from.nextLvlKarma,
                monthKarma = from.monthKarma
        )
    }
}