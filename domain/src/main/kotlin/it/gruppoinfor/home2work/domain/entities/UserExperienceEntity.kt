package it.gruppoinfor.home2work.domain.entities

data class UserExperienceEntity(
        val level: Int,
        val amount: Int,
        val currentLvLExp: Int,
        val nextLvlExp: Int
) {
    var expForNextLevel: Int = nextLvlExp - amount

    private var toNextLevelExp: Int = nextLvlExp - currentLvLExp
    private var expDelta: Int = amount - currentLvLExp
    var progress: Float = if (toNextLevelExp == 0) 0f else (100f / toNextLevelExp) * expDelta

}
