package it.gruppoinfor.home2work

import android.arch.lifecycle.MutableLiveData
import android.location.Location
import it.gruppoinfor.home2work.common.BaseViewModel
import it.gruppoinfor.home2work.common.LocalUserData
import it.gruppoinfor.home2work.common.SingleLiveEvent
import it.gruppoinfor.home2work.data.api.RetrofitException
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import it.gruppoinfor.home2work.domain.usecases.CreateShare
import it.gruppoinfor.home2work.domain.usecases.GetActiveShare
import it.gruppoinfor.home2work.domain.usecases.JoinShare
import it.gruppoinfor.home2work.entities.Share


class MainViewModel(
        private val getActiveShare: GetActiveShare,
        private val joinShare: JoinShare,
        private val createShare: CreateShare,
        private val shareMapper: Mapper<ShareEntity, Share>,
        private val localUserData: LocalUserData
) : BaseViewModel() {


    var viewState: MutableLiveData<MainViewState> = MutableLiveData()
    var errorState: SingleLiveEvent<String> = SingleLiveEvent()
    var shareEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()

    init {
        viewState.value = MainViewState()
    }

    fun createShare() {
        addDisposable(createShare.observable()
                .map { shareMapper.mapFrom(it) }
                .subscribe({
                    localUserData.currentShare = it

                    val newViewState = viewState.value?.copy(shareInProgress = true)
                    viewState.value = newViewState

                    shareEvent.value = true


                }, {

                    if (it is RetrofitException) {
                        creationError(it)
                    }

                    shareEvent.value = false

                }))

    }

    fun joinShare(shareId: Long, hostLocation: Location, joinLocation: Location) {

        // TOdo controllare distanza join
        if (hostLocation.distanceTo(joinLocation) < 1000) {
            addDisposable(joinShare.join(shareId, joinLocation.latitude, joinLocation.longitude)
                    .map { shareMapper.mapFrom(it) }
                    .subscribe({

                        localUserData.currentShare = it

                        val newViewState = viewState.value?.copy(shareInProgress = true)
                        viewState.value = newViewState

                        shareEvent.value = true


                    }, {

                        if (it is RetrofitException) {
                            joinError(it)
                        }

                        shareEvent.value = false

                    }))
        } else {
            errorState.value = "Impossibile unirsi alla condivisione: troppo distance dall'host"
        }


    }

    fun getCurrentShare() {
        addDisposable(getActiveShare.observable()
                .map { shareMapper.mapOptional(it) }
                .subscribe({

                    if (it.hasValue()) {
                        localUserData.currentShare = it.value

                        val newViewState = viewState.value?.copy(shareInProgress = true)
                        viewState.value = newViewState
                    }

                }, {

                }
                ))
    }

    private fun creationError(exception: RetrofitException) {

        errorState.value = "Impossibile creare una nuova condivisione: " + when (exception.kind) {
            RetrofitException.Kind.NETWORK -> "nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "errore sconosciuto"
        }


    }

    private fun joinError(exception: RetrofitException) {

        errorState.value = "Impossibile unirsi alla condivisione: " + when (exception.kind) {
            RetrofitException.Kind.NETWORK -> "nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "errore sconosciuto"
        }


    }

}