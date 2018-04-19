package it.gruppoinfor.home2work.domain.entities

data class KarmaEntity(
        val level: Int,
        val amount: Int,
        val currentLvLKarma: Int,
        val nextLvlKarma: Int,
        val monthKarma: Int
)
