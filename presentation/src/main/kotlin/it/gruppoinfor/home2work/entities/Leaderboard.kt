package it.gruppoinfor.home2work.entities

import java.io.Serializable

class Leaderboard {

    enum class Type constructor(val value: Int) : Serializable {
        Shares(0), Distance(1);

        companion object {
            fun from(findValue: Int): Type = Type.values().first { it.value == findValue }
        }
    }


    enum class TimeSpan constructor(val value: Int) : Serializable {
        AllTime(0), Monthly(1), Weekly(2);

        companion object {
            fun from(findValue: Int): TimeSpan = TimeSpan.values().first { it.value == findValue }
        }
    }

    enum class Range  {
        Global, Company
    }
}