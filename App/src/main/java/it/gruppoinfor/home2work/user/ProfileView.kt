package it.gruppoinfor.home2work.user


interface ProfileView {

    fun onLoading()

    fun onLoadingError(errorMessage: String)

    fun onRefresh()

    fun onRefreshDone()

    fun setProfileData(userProfile: UserProfile)

    fun showErrorMessage(errorMessage: String)

    fun onAvatarUploaded()
}