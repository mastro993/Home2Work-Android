package it.gruppoinfor.home2work.domain.entities


data class MonthActivityEntity(
        val year: Int,
        val month: Int,
        val shares: Int,
        val distance: Int
)