package it.gruppoinfor.home2work.user

import it.gruppoinfor.home2workapi.user.UserProfile


interface UserView {

    fun onLoading()

    fun onLoadingError(errorMessage: String)

    fun onRefresh()

    fun onRefreshDone()

    fun setProfileData(userProfile: UserProfile)

    fun showErrorMessage(errorMessage: String)
}