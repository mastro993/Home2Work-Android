package it.gruppoinfor.home2work.domain.entities


import java.util.*

class ProfileEntity(
        val karma: KarmaEntity,
        val stats: StatisticsEntity,
        val activity: Map<Int, SharingActivityEntity>,
        val regdate: Date
)
