package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.KarmaData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.KarmaEntity
import javax.inject.Inject


class KarmaEntityDataMapper @Inject constructor() : Mapper<KarmaEntity, KarmaData>() {

    override fun mapFrom(from: KarmaEntity): KarmaData {
        return KarmaData(
                level = from.level,
                amount = from.amount,
                currentLvLKarma = from.currentLvLKarma,
                nextLvlKarma = from.nextLvlKarma,
                monthKarma = from.monthKarma
        )
    }
}