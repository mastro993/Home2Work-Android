package it.gruppoinfor.home2work.domain.entities


import java.util.*

class ProfileEntity(
        val exp: ExperienceEntity,
        val stats: StatisticsEntity,
        val activity: Map<Int, SharingActivityEntity>,
        val regdate: Date
)
