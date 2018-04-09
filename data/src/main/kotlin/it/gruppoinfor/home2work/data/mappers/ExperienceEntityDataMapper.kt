package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.ExperienceData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ExperienceEntity
import javax.inject.Inject
import javax.inject.Singleton



class ExperienceEntityDataMapper @Inject constructor() : Mapper<ExperienceEntity, ExperienceData>() {

    override fun mapFrom(from: ExperienceEntity): ExperienceData {
        return ExperienceData(
                level = from.level,
                amount = from.amount,
                currentLvLExp = from.currentLvLExp,
                nextLvlExp = from.nextLvlExp,
                monthExp = from.monthExp
        )
    }
}