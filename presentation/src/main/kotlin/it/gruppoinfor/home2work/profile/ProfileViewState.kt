package it.gruppoinfor.home2work.profile

import it.gruppoinfor.home2work.common.views.ScreenState
import it.gruppoinfor.home2work.entities.Profile


data class ProfileViewState(
        var screenState: ScreenState? = null,
        var isRefreshing: Boolean = false,
        var profile: Profile? = null
)