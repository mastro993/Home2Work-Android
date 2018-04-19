package it.gruppoinfor.home2work.entities

class Karma(
        var level: Int,
        var amount: Int,
        var currentLvlKarma: Int,
        var nextLvlKarma: Int,
        var monthKarma: Int
) {


    val karmaForNextLevel
        get() = nextLvlKarma - amount

    var progress: Float = 0f
        get() {
            val toNextLevelExp = nextLvlKarma - currentLvlKarma
            val expDelta = amount - currentLvlKarma
            return if (toNextLevelExp == 0) 0f
            else (100f / toNextLevelExp) * expDelta
        }
}
