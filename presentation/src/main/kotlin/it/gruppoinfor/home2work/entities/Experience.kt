package it.gruppoinfor.home2work.entities

class Experience(
        var level: Int,
        var amount: Int,
        var currentLvLExp: Int,
        var nextLvlExp: Int
) {


    val expForNextLevel
        get() = nextLvlExp - amount

    var progress: Float = 0f
        get() {
            val toNextLevelExp = nextLvlExp - currentLvLExp
            val expDelta = amount - currentLvLExp
            return if (toNextLevelExp == 0) 0f
            else (100f / toNextLevelExp) * expDelta
        }
}
