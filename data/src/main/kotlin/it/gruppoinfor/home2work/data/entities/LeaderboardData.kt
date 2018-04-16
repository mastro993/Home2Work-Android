package it.gruppoinfor.home2work.data.entities

class LeaderboardData {
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