package it.gruppoinfor.home2work.di.settings

import dagger.Subcomponent
import it.gruppoinfor.home2work.settings.SettingsActivity


@SettingsScope
@Subcomponent(modules = [SettingsModule::class])
interface SettingsSubComponent {
    fun inject(settingsActivity: SettingsActivity)
}