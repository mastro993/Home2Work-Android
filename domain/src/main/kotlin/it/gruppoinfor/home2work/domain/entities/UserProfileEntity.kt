package it.gruppoinfor.home2work.domain.entities


import java.util.*

class UserProfileEntity(
        val exp: UserExperienceEntity,
        val stats: UserStatsEntity,
        val activity: List<MonthActivityEntity>,
        val regdate: Date
)
