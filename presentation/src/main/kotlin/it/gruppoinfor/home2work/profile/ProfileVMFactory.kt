package it.gruppoinfor.home2work.profile

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ProfileEntity
import it.gruppoinfor.home2work.domain.usecases.GetProfile
import it.gruppoinfor.home2work.domain.usecases.HideUserStatus
import it.gruppoinfor.home2work.domain.usecases.UpdateStatus
import it.gruppoinfor.home2work.entities.Profile


class ProfileVMFactory(
        private val getProfile: GetProfile,
        private val updateStatus: UpdateStatus,
        private val hideUserStatus: HideUserStatus,
        private val profileMapper: Mapper<ProfileEntity, Profile>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ProfileViewModel(getProfile, updateStatus, hideUserStatus, profileMapper) as T
    }
}