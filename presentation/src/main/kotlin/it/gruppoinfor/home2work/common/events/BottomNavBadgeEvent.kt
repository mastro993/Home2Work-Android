package it.gruppoinfor.home2work.common.events

data class BottomNavBadgeEvent(
        val tabPosition: Int,
        val badgeContent: String = ""
)