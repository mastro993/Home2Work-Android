package it.gruppoinfor.home2work.common

import android.app.Service
import it.gruppoinfor.home2work.common.user.LocalUserData
import it.gruppoinfor.home2work.common.user.SettingsPreferences
import it.gruppoinfor.home2work.di.DipendencyInjector
import javax.inject.Inject

abstract class DaggerService : Service() {

    @Inject
    lateinit var localUserData: LocalUserData
    @Inject
    lateinit var settingsPreferences: SettingsPreferences

    override fun onCreate() {
        super.onCreate()
        DipendencyInjector.mainComponent.inject(this)
    }
}