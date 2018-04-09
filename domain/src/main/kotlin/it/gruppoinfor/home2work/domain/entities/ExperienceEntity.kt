package it.gruppoinfor.home2work.domain.entities

data class ExperienceEntity(
        val level: Int,
        val amount: Int,
        val currentLvLExp: Int,
        val nextLvlExp: Int,
        val monthExp: Int
)
