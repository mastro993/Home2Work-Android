package it.gruppoinfor.home2work.user

import it.gruppoinfor.home2work.entities.Profile


interface UserView {

    fun onLoading()

    fun onLoadingError(errorMessage: String)

    fun onRefresh()

    fun onRefreshDone()

    fun setProfileData(userProfile: Profile)

    fun showErrorMessage(errorMessage: String)
}