package it.gruppoinfor.home2work.sharecurrent

import it.gruppoinfor.home2work.common.views.ScreenState
import it.gruppoinfor.home2work.entities.Guest
import it.gruppoinfor.home2work.entities.Share


data class CurrentShareViewState(
        var screenState: ScreenState? = null,
        var share: Share? = null,
        var guests: List<Guest> = listOf()
)