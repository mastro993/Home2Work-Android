package it.gruppoinfor.home2work.user

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ProfileEntity
import it.gruppoinfor.home2work.domain.usecases.GetUserProfile
import it.gruppoinfor.home2work.entities.Profile


class UserVMFactory(
        private val getUserProfile: GetUserProfile,
        private val mapper: Mapper<ProfileEntity, Profile>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return UserViewModel(getUserProfile, mapper) as T
    }
}