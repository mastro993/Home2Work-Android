package it.gruppoinfor.home2work.user

import android.content.Context
import android.net.Uri

interface UserPresenter {

    fun onCreate()

    fun onRefresh()

    fun onPause()


}