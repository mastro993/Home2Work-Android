package it.gruppoinfor.home2work.entities

class Leaderboard {
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