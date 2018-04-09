package it.gruppoinfor.home2work.common.mappers

import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ExperienceEntity
import it.gruppoinfor.home2work.entities.Experience
import javax.inject.Inject


class ExperienceEntityExperienceMapper @Inject constructor() : Mapper<ExperienceEntity, Experience>() {
    override fun mapFrom(from: ExperienceEntity): Experience {
        return Experience(
                level = from.level,
                amount = from.amount,
                currentLvLExp = from.currentLvLExp,
                nextLvlExp = from.nextLvlExp,
                monthExp = from.monthExp
        )
    }
}