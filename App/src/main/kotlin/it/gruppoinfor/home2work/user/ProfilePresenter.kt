package it.gruppoinfor.home2work.user

import android.content.Context
import android.net.Uri

interface ProfilePresenter {

    fun onViewCreated()

    fun onRefresh()

    fun onPause()

    fun uploadAvatar(context: Context, uri: Uri)

}