package it.gruppoinfor.home2work.settings

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import it.gruppoinfor.home2work.profile.ProfileViewModel

class SettingsVMFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SettingsViewModel() as T
    }
}