package it.gruppoinfor.home2work.splash

import android.arch.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import it.gruppoinfor.home2work.common.BaseViewModel
import it.gruppoinfor.home2work.common.LocalUserData
import it.gruppoinfor.home2work.common.SingleLiveEvent
import it.gruppoinfor.home2work.data.api.RetrofitException
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.UserEntity
import it.gruppoinfor.home2work.domain.usecases.UserTokenLogin
import it.gruppoinfor.home2work.entities.User
import it.gruppoinfor.home2work.firebase.FirebaseTokenService


class SplashViewModel(
        private val userTokenLogin: UserTokenLogin,
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

        val token = localUserData.token

        token?.let {
            addDisposable(userTokenLogin.login(it)
                    .map {
                        localUserData.token = it.accessToken
                        userEntityUserMapper.mapFrom(it)
                    }
                    .subscribe({
                        onLoginSuccess(it)
                    }, {
                        if (it is RetrofitException) {
                            onError(it)
                        }
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

    private fun onError(retrofitException: RetrofitException) {

        when (retrofitException.kind) {
            RetrofitException.Kind.NETWORK -> errorState.value = "Nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> errorState.value = "Sessione scaduta"
            RetrofitException.Kind.UNEXPECTED -> errorState.value = "Errore sconosciuto"
        }

        val newState = viewState.value?.copy(
                isLoading = false,
                showSignInButton = true
        )
        viewState.value = newState

    }

}