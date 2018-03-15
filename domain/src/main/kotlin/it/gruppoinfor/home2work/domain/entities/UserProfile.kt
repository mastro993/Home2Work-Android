package it.gruppoinfor.home2work.domain.entities


import it.gruppoinfor.home2work.domain.entities.UserExperience
import java.util.*

class UserProfile(
        val exp: UserExperience,
        val stats: UserStats,
        val activity: List<MonthActivity>,
        val regdate: Date
)
