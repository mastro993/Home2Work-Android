package it.gruppoinfor.home2work.signin

import android.arch.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import it.gruppoinfor.home2work.common.BaseViewModel
import it.gruppoinfor.home2work.common.SingleLiveEvent
import it.gruppoinfor.home2work.services.FirebaseTokenService
import it.gruppoinfor.home2work.common.LocalUserData
import it.gruppoinfor.home2work.data.api.APIAuthenticationInterceptor
import it.gruppoinfor.home2work.data.api.RetrofitException
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.UserEntity
import it.gruppoinfor.home2work.domain.usecases.UserLogin
import it.gruppoinfor.home2work.entities.User


class SignInViewModel(
        private val userLogin: UserLogin,
        private val userEntityUserMapper: Mapper<UserEntity, User>,
        private val localUserData: LocalUserData
) : BaseViewModel() {


    var viewState: MutableLiveData<SignInViewState> = MutableLiveData()
    var errorState: SingleLiveEvent<String> = SingleLiveEvent()
    var loginSuccessState: SingleLiveEvent<Boolean> = SingleLiveEvent()

    init {
        viewState.value = SignInViewState()
    }

    fun loadSavedEmail() {
        val email = localUserData.email

        val newViewState = viewState.value?.copy(
                savedEmail = email
        )

        viewState.value = newViewState

    }

    fun login(email: String, password: String) {

        addDisposable(userLogin.login(email, password)
                .map {
                    userEntityUserMapper.mapFrom(it)
                }
                .doOnSubscribe {
                    val newViewState = viewState.value?.copy(
                            isLoading = true
                    )
                    viewState.value = newViewState
                }
                .subscribe({
                    onLoginSuccess(it)
                }, {

                    if(it is RetrofitException){
                        when(it.kind){
                            RetrofitException.Kind.SERVER -> errorState.value = "Errore durante il login"
                            RetrofitException.Kind.CLIENT -> errorState.value = "Dati inseriti non validi"
                            RetrofitException.Kind.NETWORK -> errorState.value = "Nessuna connessione ad internet"
                            else -> errorState.value = "Errore imprevisto"
                        }
                    }

                    loginSuccessState.value = false

                    val newViewState = viewState.value?.copy(
                            isLoading = false
                    )
                    viewState.value = newViewState

                })
        )
    }

    private fun onLoginSuccess(user: User) {

        // Salvo i dati dell'utente per crashalytics
        Crashlytics.setUserIdentifier(user.id.toString())
        Crashlytics.setUserEmail(user.email)
        Crashlytics.setUserName(user.fullName)

        localUserData.session = APIAuthenticationInterceptor.sessionToken

        // Aggiorno il token Firebase Cloud Messaging sul server
        FirebaseTokenService().onTokenRefresh()

        localUserData.user = user


        loginSuccessState.value = true

    }


}