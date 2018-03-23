package it.gruppoinfor.home2work.sharehistory

import android.arch.lifecycle.MutableLiveData
import it.gruppoinfor.home2work.common.BaseViewModel
import it.gruppoinfor.home2work.common.ScreenState
import it.gruppoinfor.home2work.common.SingleLiveEvent
import it.gruppoinfor.home2work.data.api.RetrofitException
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import it.gruppoinfor.home2work.domain.usecases.GetShareList
import it.gruppoinfor.home2work.entities.Share

class ShareHistoryViewModel(
        private val getShareList: GetShareList,
        private val mapper: Mapper<ShareEntity, Share>
) : BaseViewModel() {

    var viewState: MutableLiveData<ShareHistoryViewState> = MutableLiveData()
    var errorState: SingleLiveEvent<String> = SingleLiveEvent()

    init {
        viewState.value = ShareHistoryViewState()
    }


    fun getShareList() {

        addDisposable(getShareList.observable()
                .map { it.map { mapper.mapFrom(it) } }
                .doOnSubscribe {
                    val newViewState = viewState.value?.copy(
                            screenState = ScreenState.Loading
                    )
                    viewState.value = newViewState
                }
                .subscribe({

                    if (it.isEmpty()) {
                        val newViewState = viewState.value?.copy(
                                screenState = ScreenState.Empty("Non hai ancora completato nessuna condivisione")
                        )
                        viewState.value = newViewState
                    } else {
                        val newViewState = viewState.value?.copy(
                                screenState = ScreenState.Done,
                                sharesHistory = it
                        )
                        viewState.value = newViewState
                    }


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


}