package it.gruppoinfor.home2work.user

import android.arch.lifecycle.MutableLiveData
import it.gruppoinfor.home2work.common.BaseViewModel
import it.gruppoinfor.home2work.common.SingleLiveEvent
import it.gruppoinfor.home2work.common.views.ScreenState
import it.gruppoinfor.home2work.data.api.RetrofitException
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ProfileEntity
import it.gruppoinfor.home2work.domain.usecases.GetUserProfile
import it.gruppoinfor.home2work.entities.Profile

class UserViewModel(
        private val getUserProfile: GetUserProfile,
        private val profileMapper: Mapper<ProfileEntity, Profile>
) : BaseViewModel() {

    var viewState: MutableLiveData<UserViewState> = MutableLiveData()
    var errorState: SingleLiveEvent<String> = SingleLiveEvent()
    var authEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()

    init {
        viewState.value = UserViewState()
    }


    fun getProfile(userId: Long) {

        addDisposable(getUserProfile.getById(userId)
                .map { profileMapper.mapFrom(it) }
                .doOnSubscribe {
                    val newViewState = viewState.value?.copy(
                            screenState = ScreenState.Loading
                    )
                    viewState.value = newViewState
                }
                .subscribe({

                    val newViewState = viewState.value?.copy(
                            screenState = ScreenState.Done,
                            profile = it
                    )
                    viewState.value = newViewState

                }, {

                    with(it as RetrofitException) {
                        val errorMessage = when (kind) {
                            RetrofitException.Kind.NETWORK -> "Nessuna connessione ad internet"
                            RetrofitException.Kind.HTTP -> "Impossibile contattare il server"
                            RetrofitException.Kind.UNEXPECTED -> "Errore sconosciuto"
                            else ->{""}
                        }

                        val newViewState = viewState.value?.copy(
                                screenState = ScreenState.Error(errorMessage)
                        )
                        viewState.value = newViewState
                    }

                }))
    }

    fun refreshProfile(userId: Long) {
        addDisposable(getUserProfile.getById(userId)
                .map { profileMapper.mapFrom(it) }
                .doOnSubscribe {
                    val newViewState = viewState.value?.copy(
                            isRefreshing = true
                    )
                    viewState.value = newViewState
                }
                .subscribe({
                    val newViewState = viewState.value?.copy(
                            isRefreshing = false,
                            profile = it
                    )
                    viewState.value = newViewState


                }, {

                    val newViewState = viewState.value?.copy(
                            isRefreshing = false
                    )
                    viewState.value = newViewState

                }))
    }


}