package it.gruppoinfor.home2work.domain.entities

class LeaderboardEntity {
    enum class Type {
        Shares, Distance
    }

    enum class Range {
        Global, Company
    }

    enum class TimeSpan {
        AllTime, Monthly, Weekly
    }
}