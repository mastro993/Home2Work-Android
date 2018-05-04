package it.gruppoinfor.home2work.signin

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import it.gruppoinfor.home2work.common.LocalUserData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.UserEntity
import it.gruppoinfor.home2work.domain.usecases.UserLogin
import it.gruppoinfor.home2work.entities.User


class SignInVMFactory(
        private val useCase: UserLogin,
        private val mapper: Mapper<UserEntity, User>,
        private val localUserData: LocalUserData
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SignInViewModel(useCase, mapper, localUserData) as T
    }

}