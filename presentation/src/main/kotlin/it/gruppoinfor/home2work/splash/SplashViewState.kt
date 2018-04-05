package it.gruppoinfor.home2work.splash

import it.gruppoinfor.home2work.common.BaseViewState


data class SplashViewState(
        var isLoading: Boolean = true,
        var showSignInButton: Boolean = false
): BaseViewState()