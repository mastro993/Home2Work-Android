package it.gruppoinfor.home2work.sharecurrent

import android.arch.lifecycle.MutableLiveData
import android.location.Location
import it.gruppoinfor.home2work.common.BaseViewModel
import it.gruppoinfor.home2work.common.SingleLiveEvent
import it.gruppoinfor.home2work.common.views.ScreenState
import it.gruppoinfor.home2work.data.api.RetrofitException
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import it.gruppoinfor.home2work.domain.usecases.*
import it.gruppoinfor.home2work.entities.Share

class CurrentShareViewModel(
        private val getActiveShare: GetActiveShare,
        private val banUserFromShare: BanUserFromShare,
        private val cancelCurrentShare: CancelCurrentShare,
        private val leaveShare: LeaveShare,
        private val completeCurrentShare: CompleteCurrentShare,
        private val finishCurrentShare: FinishCurrentShare,
        private val shareMapper: Mapper<ShareEntity, Share>
) : BaseViewModel() {

    var viewState: MutableLiveData<CurrentShareViewState> = MutableLiveData()
    var errorEvent: SingleLiveEvent<String> = SingleLiveEvent()
    var infoEvent: SingleLiveEvent<String> = SingleLiveEvent()
    var shareFinishEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()

    init {
        viewState.value = CurrentShareViewState()
    }

    fun getActiveShare() {

        addDisposable(getActiveShare.observable()
                .map { shareMapper.mapFrom(it) }
                .doOnSubscribe {

                    val newViewState = viewState.value?.copy(
                            screenState = ScreenState.Loading
                    )
                    viewState.value = newViewState

                }
                .subscribe({

                    val newViewState = viewState.value?.copy(
                            screenState = ScreenState.Done,
                            share = it
                    )
                    viewState.value = newViewState


                }, {

                    if (it is RetrofitException) {
                        val errorMessage = when (it.kind) {
                            RetrofitException.Kind.NETWORK -> "Nessuna connessione ad internet"
                            RetrofitException.Kind.HTTP -> "Impossibile contattare il server"
                            RetrofitException.Kind.UNEXPECTED -> "Errore sconosciuto"
                        }

                        val newViewState = viewState.value?.copy(
                                screenState = ScreenState.Error(errorMessage)
                        )
                        viewState.value = newViewState
                    }


                }))

    }

    fun refreshActiveShare() {

        addDisposable(getActiveShare.observable()
                .map { shareMapper.mapFrom(it) }
                .subscribe({


                    val newViewState = viewState.value?.copy(
                            share = it
                    )
                    viewState.value = newViewState


                }, {


                }))

    }

    fun banUser(userId: Long) {
        addDisposable(banUserFromShare.banById(userId)
                .subscribe({

                    if (it) {
                        infoEvent.value = "Utente espulso"
                        refreshActiveShare()
                    } else {
                        errorEvent.value = "Impossibile espellere l'utente selezionato"
                    }

                }, {

                    errorEvent.value = "Impossibile espellere l'utente selezionato"

                }))
    }

    fun cancelShare() {
        addDisposable(cancelCurrentShare.observable()
                .subscribe({

                    if (it) {
                        shareFinishEvent.value = true
                    } else {
                        errorEvent.value = "Impossibile interrompere la condivisione corso"
                    }
                }, {

                    errorEvent.value = "Impossibile interrompere la condivisione in corso"

                }))
    }

    fun leaveShare() {
        addDisposable(leaveShare.observable()
                .subscribe({

                    if (it) {
                        shareFinishEvent.value = true
                    } else {
                        errorEvent.value = "Impossibile abbandonare la condivisione corso"
                    }
                }, {

                    errorEvent.value = "Impossibile abbandonare la condivisione in corso"

                }))
    }

    fun completeShare(hostLocation: Location, completeLocation: Location) {

        if (hostLocation.distanceTo(completeLocation) < 1000) {
            addDisposable(completeCurrentShare.completeFrom(completeLocation.latitude, completeLocation.longitude)
                    .subscribe({

                        if (it) {
                            shareFinishEvent.value = true
                        } else {
                            errorEvent.value = "Impossibile completare la condivisione corso"
                        }
                    }, {

                        errorEvent.value = "Impossibile completare la condivisione in corso"

                    }))
        } else {
            errorEvent.value = "Impossibile completare la condivisione in corso"
        }

    }

    fun finishShare() {
        addDisposable(finishCurrentShare.observable()
                .subscribe({

                    if (it) {
                        shareFinishEvent.value = true
                    } else {
                        errorEvent.value = "Impossibile completare la condivisione corso"
                    }
                }, {

                    errorEvent.value = "Impossibile completare la condivisione in corso"

                }))
    }


}