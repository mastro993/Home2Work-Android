package it.gruppoinfor.home2work.main

import android.arch.lifecycle.MutableLiveData
import android.location.Location
import it.gruppoinfor.home2work.common.BaseViewModel
import it.gruppoinfor.home2work.common.SingleLiveEvent
import it.gruppoinfor.home2work.common.user.LocalUserData
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
                .doOnSubscribe {

                    val newViewState = viewState.value?.copy(
                            creatingShare = true
                    )

                    viewState.value = newViewState

                }
                .subscribe({

                    localUserData.currentShare = it

                    val newViewState = viewState.value?.copy(
                            creatingShare = false,
                            shareInProgress = true
                    )
                    viewState.value = newViewState

                    shareEvent.value = true

                }, {

                    val newViewState = viewState.value?.copy(
                            creatingShare = false
                    )

                    viewState.value = newViewState

                    with(it as RetrofitException) {
                        when (kind) {
                            RetrofitException.Kind.NETWORK -> errorState.value = "Impossibile contattare il server"
                            RetrofitException.Kind.HTTP -> errorState.value = "Il server ha riscontrato un problema"
                            RetrofitException.Kind.UNEXPECTED -> errorState.value = "Errore sconosciuto"
                        }
                    }
                    shareEvent.value = false

                }))

    }

    fun joinShare(shareId: Long, hostLocation: Location, joinLocation: Location) {

        if (hostLocation.distanceTo(joinLocation) < 1000) {
            addDisposable(joinShare.join(shareId, joinLocation.latitude, joinLocation.longitude)
                    .map { shareMapper.mapFrom(it) }
                    .subscribe({

                        localUserData.currentShare = it

                        val newViewState = viewState.value?.copy(shareInProgress = true)
                        viewState.value = newViewState

                        shareEvent.value = true


                    }, {

                        with(it as RetrofitException) {
                            when (kind) {
                                RetrofitException.Kind.NETWORK -> errorState.value = "Impossibile contattare il server"
                                RetrofitException.Kind.HTTP -> errorState.value = "Il server ha riscontrato un problema"
                                RetrofitException.Kind.UNEXPECTED -> errorState.value = "Errore sconosciuto"
                            }
                        }

                        shareEvent.value = false

                    }))
        } else {
            errorState.value = "Impossibile unirsi alla condivisione: troppo distante dall'host"
        }


    }

    fun getCurrentShare() {
        addDisposable(getActiveShare.observable()
                .map { shareMapper.mapFrom(it) }
                .subscribe({

                    localUserData.currentShare = it

                    val newViewState = viewState.value?.copy(shareInProgress = it != null)
                    viewState.value = newViewState

                }, {
                    val newViewState = viewState.value?.copy(shareInProgress = false)
                    viewState.value = newViewState
                }
                ))
    }


}