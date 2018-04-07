package it.gruppoinfor.home2work.singlechat

import android.arch.lifecycle.MutableLiveData
import it.gruppoinfor.home2work.common.BaseViewModel
import it.gruppoinfor.home2work.common.SingleLiveEvent
import it.gruppoinfor.home2work.common.events.NewMessageEvent
import it.gruppoinfor.home2work.common.user.LocalUserData
import it.gruppoinfor.home2work.common.views.ScreenState
import it.gruppoinfor.home2work.data.api.RetrofitException
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ChatMessageEntity
import it.gruppoinfor.home2work.domain.usecases.GetChatMessage
import it.gruppoinfor.home2work.domain.usecases.GetChatMessageList
import it.gruppoinfor.home2work.domain.usecases.NewChat
import it.gruppoinfor.home2work.domain.usecases.SendMessage
import it.gruppoinfor.home2work.entities.ChatMessage


class SingleChatViewModel(
        private var newChat: NewChat,
        private var getChatMessageList: GetChatMessageList,
        private var getMessage: GetChatMessage,
        private var sendMessage: SendMessage,
        private var messageMapper: Mapper<ChatMessageEntity, ChatMessage>,
        private var entityMapper: Mapper<ChatMessage, ChatMessageEntity>,
        private var localUserData: LocalUserData
) : BaseViewModel() {

    var chatId: Long? = null
    var userId: Long? = null
    var viewStateSingle: MutableLiveData<SingleChatViewState> = MutableLiveData()
    var messageSent: SingleLiveEvent<ChatMessage> = SingleLiveEvent()
    var messageReceived: SingleLiveEvent<ChatMessage> = SingleLiveEvent()
    var errorState: SingleLiveEvent<String> = SingleLiveEvent()

    init {
        viewStateSingle.value = SingleChatViewState()
    }

    fun createChat() {

        userId?.let {
            addDisposable(newChat.withUserId(it)
                    .doOnSubscribe {
                        val newViewState = viewStateSingle.value?.copy(
                                screenState = ScreenState.Loading
                        )
                        viewStateSingle.value = newViewState
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
                                else -> {
                                    ""
                                }
                            }

                            val newViewState = viewStateSingle.value?.copy(
                                    screenState = ScreenState.Error(errorMessage)
                            )
                            viewStateSingle.value = newViewState
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
                        val newViewState = viewStateSingle.value?.copy(
                                screenState = ScreenState.Loading
                        )
                        viewStateSingle.value = newViewState
                    }
                    .subscribe({

                        val newViewState = if (it.isEmpty()) {
                            viewStateSingle.value?.copy(
                                    screenState = ScreenState.Empty("Scrivi tu il primo messaggio")
                            )
                        } else {
                            viewStateSingle.value?.copy(
                                    screenState = ScreenState.Done,
                                    messageList = it
                            )
                        }

                        viewStateSingle.value = newViewState

                    }, {

                        if (it is RetrofitException) {
                            val errorMessage = when (it.kind) {
                                RetrofitException.Kind.NETWORK -> "Nessuna connessione ad internet"
                                RetrofitException.Kind.HTTP -> "Impossibile contattare il server"
                                RetrofitException.Kind.UNEXPECTED -> "Errore sconosciuto"
                                else -> {
                                    ""
                                }
                            }

                            val newViewState = viewStateSingle.value?.copy(
                                    screenState = ScreenState.Error(errorMessage)
                            )
                            viewStateSingle.value = newViewState
                        }


                    })
            )
        }
    }

    private fun getMessage(messageId: Long) {
        addDisposable(getMessage.getById(messageId)
                .map {
                    messageMapper.mapFrom(it)
                }
                .subscribe({
                    messageReceived.value = it
                }, {

                }))
    }

    private fun silentRefreshMessageList() {
        chatId?.let {
            addDisposable(getChatMessageList.getById(it)
                    .map { it.map { messageMapper.mapFrom(it) } }
                    .subscribe({

                        messageReceived.value = it.last()
                    }, {

                    })
            )
        }
    }

    fun sendMessage(message: String) {
        chatId?.let {
            addDisposable(sendMessage.send(it, message)
                    .map { messageMapper.mapFrom(it) }
                    .subscribe({

                        val newViewState = viewStateSingle.value?.copy(
                                screenState = ScreenState.Done
                        )
                        viewStateSingle.value = newViewState
                        messageSent.value = it


                    }, {

                    }))
        }
    }

    fun onNewMessageEvent(newMessageEvent: NewMessageEvent) {

        if (newMessageEvent.chatId == chatId) {
            newMessageEvent.messageId?.let {
                getMessage(it)
            }
        }

    }

}