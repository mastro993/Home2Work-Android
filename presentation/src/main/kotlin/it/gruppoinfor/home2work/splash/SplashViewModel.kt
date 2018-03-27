package it.gruppoinfor.home2work.splash

import android.arch.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import it.gruppoinfor.home2work.common.BaseViewModel
import it.gruppoinfor.home2work.common.SingleLiveEvent
import it.gruppoinfor.home2work.common.services.FirebaseTokenService
import it.gruppoinfor.home2work.common.user.LocalUserData
import it.gruppoinfor.home2work.data.api.APIAuthenticationInterceptor
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.UserEntity
import it.gruppoinfor.home2work.domain.usecases.GetUser
import it.gruppoinfor.home2work.entities.User


class SplashViewModel(
        private val getUser: GetUser,
        private val userEntityUserMapper: Mapper<UserEntity, User>,
        private val localUserData: LocalUserData
) : BaseViewModel() {

    var viewState: MutableLiveData<SplashViewState> = MutableLiveData()
    var errorState: SingleLiveEvent<String> = SingleLiveEvent()
    var loginState: SingleLiveEvent<Boolean> = SingleLiveEvent()

    init {
        viewState.value = SplashViewState()
    }

    fun tokenLogin() {

        localUserData.session?.let {

            APIAuthenticationInterceptor.sessionToken = it

            addDisposable(getUser.observable()
                    .map {
                        userEntityUserMapper.mapFrom(it)
                    }
                    .subscribe({
                        onLoginSuccess(it)
                    }, {
                        val newState = viewState.value?.copy(
                                isLoading = false,
                                showSignInButton = true
                        )
                        viewState.value = newState
                    }))

        } ?: onEmptyToken()

    }

    private fun onEmptyToken() {

        val newState = viewState.value?.copy(
                isLoading = false,
                showSignInButton = true
        )
        viewState.value = newState

    }

    private fun onLoginSuccess(user: User) {

        // Salvo i dati dell'utente per crashalytics
        Crashlytics.setUserIdentifier(user.id.toString())
        Crashlytics.setUserEmail(user.email)
        Crashlytics.setUserName(user.fullName)

        // Aggiorno il token Firebase Cloud Messaging sul server
        FirebaseTokenService().onTokenRefresh()

        localUserData.user = user

        loginState.value = true

    }

}