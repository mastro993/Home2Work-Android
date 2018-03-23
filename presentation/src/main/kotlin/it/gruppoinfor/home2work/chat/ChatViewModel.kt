package it.gruppoinfor.home2work.chat

import android.arch.lifecycle.MutableLiveData
import it.gruppoinfor.home2work.common.BaseViewModel
import it.gruppoinfor.home2work.common.LocalUserData
import it.gruppoinfor.home2work.common.ScreenState
import it.gruppoinfor.home2work.common.SingleLiveEvent
import it.gruppoinfor.home2work.data.api.RetrofitException
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.usecases.GetChatMessageList
import it.gruppoinfor.home2work.domain.usecases.NewChat
import it.gruppoinfor.home2work.domain.usecases.SendMessage
import it.gruppoinfor.home2work.entities.ChatMessage
import it.gruppoinfor.home2work.events.NewMessageEvent


class ChatViewModel(
        private var newChat: NewChat,
        private var getChatMessageList: GetChatMessageList,
        private var sendMessage: SendMessage,
        private var messageMapper: Mapper<ChatMessageEntity, ChatMessage>,
        private var entityMapper: Mapper<ChatMessage, ChatMessageEntity>,
        private var localUserData: LocalUserData
) : BaseViewModel() {

    var chatId: Long? = null
    var userId: Long? = null
    var viewState: MutableLiveData<ChatViewState> = MutableLiveData()
    var errorState: SingleLiveEvent<String> = SingleLiveEvent()

    init {
        viewState.value = ChatViewState()
    }

    fun createChat() {
        userId?.let {
            addDisposable(newChat.withUserId(it)
                    .doOnSubscribe {
                        val newViewState = viewState.value?.copy(
                                screenState = ScreenState.Loading
                        )
                        viewState.value = newViewState
                    }
                    .subscribe({
                        chatId = it.id
                        getMessageList()
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


                    })
            )
        }

    }

    fun getMessageList() {
        chatId?.let {
            addDisposable(getChatMessageList.getById(it)
                    .map { it.map { messageMapper.mapFrom(it) } }
                    .doOnSubscribe {
                        val newViewState = viewState.value?.copy(
                                screenState = ScreenState.Loading
                        )
                        viewState.value = newViewState
                    }
                    .subscribe({

                        val newViewState = if (it.isEmpty()) {
                            viewState.value?.copy(
                                    screenState = ScreenState.Empty("Scrivi tu il primo messaggio")
                            )
                        } else {
                            viewState.value?.copy(
                                    screenState = ScreenState.Done,
                                    messageList = it
                            )
                        }

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


                    })
            )
        }
    }

    private fun silentRefreshMessageList() {
        chatId?.let {
            addDisposable(getChatMessageList.getById(it)
                    .map { it.map { messageMapper.mapFrom(it) } }
                    .subscribe({
                        val newViewState = viewState.value?.copy(
                                messageList = it
                        )
                        viewState.value = newViewState
                    }, {

                    })
            )
        }
    }

    fun sendMessage(message: String) {

        chatId?.let {
            addDisposable(sendMessage.send(it, message)
                    .subscribe({

                        if (it) {
                            silentRefreshMessageList()
                        }

                    }, {

                    }))
        }
    }

    fun onNewMessageEvent(newMessageEvent: NewMessageEvent) {

        if (newMessageEvent.chatId == chatId) {
            silentRefreshMessageList()
        }

    }

    private fun loadingError(exception: RetrofitException) {

        val errorMessage = when (exception.kind) {
            RetrofitException.Kind.NETWORK -> "Nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "Impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "Errore sconosciuto"
        }

    }

}