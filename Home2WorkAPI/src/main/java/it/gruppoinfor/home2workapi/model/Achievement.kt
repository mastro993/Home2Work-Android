package it.gruppoinfor.home2workapi.model

import it.gruppoinfor.home2workapi.HomeToWorkClient
import java.io.Serializable
import java.util.*


class Achievement : Serializable {

    var achievementID: Long = 0
    var name: String = "null"
    var description: String = "null"
    var karma: Int = 0
    var exp: Int = 0
    var unlockDate: Date = Date()
    var goal: Double = 1.0
    var current: Double = 1.0

    val progress: Int
        get() = (100.0 / goal * current).toInt()

    val iconURL: String
        get() = HomeToWorkClient.ACHIEVEMENTS_BASE_URL + achievementID + ".jpg"
}
