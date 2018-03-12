package it.gruppoinfor.home2work.user

import android.content.Context
import android.net.Uri
import java.net.URI

interface ProfilePresenter {

    fun onViewCreated()

    fun onRefresh()

    fun onPause()

    fun uploadAvatar(context: Context, uri: Uri)

}