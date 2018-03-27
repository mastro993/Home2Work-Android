package it.gruppoinfor.home2work.user

import it.gruppoinfor.home2work.common.views.ScreenState
import it.gruppoinfor.home2work.entities.Profile


data class UserViewState(
        var screenState: ScreenState? = null,
        var isRefreshing: Boolean = false,
        var profile: Profile? = null
)