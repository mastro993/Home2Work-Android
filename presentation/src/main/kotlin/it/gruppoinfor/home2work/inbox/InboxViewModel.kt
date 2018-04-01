package it.gruppoinfor.home2work.inbox

import android.arch.lifecycle.MutableLiveData
import it.gruppoinfor.home2work.common.BaseViewModel
import it.gruppoinfor.home2work.common.SingleLiveEvent
import it.gruppoinfor.home2work.common.views.ScreenState
import it.gruppoinfor.home2work.data.api.RetrofitException
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ChatEntity
import it.gruppoinfor.home2work.domain.usecases.GetChatList
import it.gruppoinfor.home2work.entities.Chat


class InboxViewModel(
        private val getChatList: GetChatList,
        private val mapper: Mapper<ChatEntity, Chat>
) : BaseViewModel() {

    var viewState: MutableLiveData<InboxViewState> = MutableLiveData()
    var errorState: SingleLiveEvent<String> = SingleLiveEvent()

    init {
        viewState.value = InboxViewState()
    }

    fun getChatList() {

        addDisposable(getChatList.observable()
                .map {
                    it.map {
                        mapper.mapFrom(it)
                    }
                }
                .doOnSubscribe {
                    val newViewState = viewState.value?.copy(
                            screenState = ScreenState.Loading,
                            isRefreshing = false
                    )
                    viewState.value = newViewState
                }
                .subscribe({

                    val newViewState = if (it.isEmpty()) {
                        viewState.value?.copy(
                                screenState = ScreenState.Empty("Non hai ancora nessuna conversazione")
                        )
                    } else {
                        viewState.value?.copy(
                                screenState = ScreenState.Done,
                                chatList = it
                        )
                    }
                    viewState.value = newViewState

                }, {

                    if (it is RetrofitException) {

                        val errorString = when (it.kind) {
                            RetrofitException.Kind.NETWORK -> "Nessuna connessione ad internet"
                            RetrofitException.Kind.HTTP -> "Impossibile contattare il server"
                            RetrofitException.Kind.UNEXPECTED -> "Errore sconosciuto"
                        }


                        viewState.value = viewState.value?.copy(
                                screenState = ScreenState.Error(errorString)
                        )

                    }

                }))
    }

    fun refreshChatList() {
        addDisposable(getChatList.observable()
                .map {
                    it.map { mapper.mapFrom(it) }
                }
                .doOnSubscribe {
                    val newViewState = viewState.value?.copy(
                            isRefreshing = true
                    )
                    viewState.value = newViewState
                }
                .subscribe({

                    val newViewState = if (it.isEmpty()) {
                        viewState.value?.copy(
                                isRefreshing = false
                        )
                    } else {
                        viewState.value?.copy(
                                isRefreshing = false,
                                chatList = it
                        )
                    }
                    viewState.value = newViewState

                }, {

                    if (it is RetrofitException) {

                        val errorString = when (it.kind) {
                            RetrofitException.Kind.NETWORK -> "Nessuna connessione ad internet"
                            RetrofitException.Kind.HTTP -> "Impossibile contattare il server"
                            RetrofitException.Kind.UNEXPECTED -> "Errore sconosciuto"
                        }

                        errorState.value = errorString


                    }

                    viewState.value = viewState.value?.copy(
                            isRefreshing = false
                    )

                }))
    }

    fun silentRefreshChatList() {
        addDisposable(getChatList.observable()
                .map {
                    it.map { mapper.mapFrom(it) }
                }
                .subscribe({

                    if (!it.isEmpty()) {
                        val newViewState = viewState.value?.copy(
                                chatList = it
                        )
                        viewState.value = newViewState
                    }


                }, {
                    it.printStackTrace()
                }))
    }

}