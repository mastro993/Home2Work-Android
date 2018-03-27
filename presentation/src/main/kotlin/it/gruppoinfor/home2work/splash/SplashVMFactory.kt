package it.gruppoinfor.home2work.splash

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import it.gruppoinfor.home2work.common.user.LocalUserData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.UserEntity
import it.gruppoinfor.home2work.domain.usecases.GetUser
import it.gruppoinfor.home2work.entities.User


@Suppress("UNCHECKED_CAST")
class SplashVMFactory(
        private val getUser: GetUser,
        private val mapper: Mapper<UserEntity, User>,
        private val localUserData: LocalUserData
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SplashViewModel(getUser, mapper, localUserData) as T
    }

}