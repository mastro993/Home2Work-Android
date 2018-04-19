package it.gruppoinfor.home2work.profile

import android.arch.lifecycle.MutableLiveData
import it.gruppoinfor.home2work.common.BaseViewModel
import it.gruppoinfor.home2work.common.SingleLiveEvent
import it.gruppoinfor.home2work.common.views.ScreenState
import it.gruppoinfor.home2work.data.api.RetrofitException
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ProfileEntity
import it.gruppoinfor.home2work.domain.usecases.GetProfile
import it.gruppoinfor.home2work.domain.usecases.UpdateStatus
import it.gruppoinfor.home2work.entities.Profile


class ProfileViewModel(
        private val getProfile: GetProfile,
        private val updateStatus: UpdateStatus,
        private val profileMapper: Mapper<ProfileEntity, Profile>
) : BaseViewModel() {

    var viewState: MutableLiveData<ProfileViewState> = MutableLiveData()
    var errorState: SingleLiveEvent<String> = SingleLiveEvent()

    init {
        viewState.value = ProfileViewState()
    }

    fun getProfile() {

        addDisposable(getProfile.observable()
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

                    if (it is RetrofitException) {
                        val errorMessage = when (it.kind) {
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

    fun refreshProfile() {
        addDisposable(getProfile.observable()
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

                    if (it is RetrofitException) {
                        val errorMessage = when (it.kind) {
                            RetrofitException.Kind.NETWORK -> "Nessuna connessione ad internet"
                            RetrofitException.Kind.HTTP -> "Impossibile contattare il server"
                            RetrofitException.Kind.UNEXPECTED -> "Errore sconosciuto"
                            else ->{""}
                        }

                        errorState.value = errorMessage
                    }

                    val newViewState = viewState.value?.copy(
                            isRefreshing = false
                    )
                    viewState.value = newViewState

                }))
    }

}