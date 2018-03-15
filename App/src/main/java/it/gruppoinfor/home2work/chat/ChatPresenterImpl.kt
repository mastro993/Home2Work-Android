package it.gruppoinfor.home2work.chat

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import it.gruppoinfor.home2work.firebase.NewMessageEvent
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.RetrofitException
import it.gruppoinfor.home2workapi.chat.Author
import it.gruppoinfor.home2workapi.chat.Message


class ChatPresenterImpl constructor(private val chatView: ChatView) : ChatPresenter {

    private lateinit var mCompositeDisposable: CompositeDisposable
    private var chatId: Long = 0L
    private var recipientId: Long = 0L

    private var cache: ArrayList<Message> = ArrayList()

    override fun setChatId(chatId: Long) {
        this.chatId = chatId
    }

    override fun setRecipientId(recipientId: Long) {
        this.recipientId = recipientId
    }

    override fun onResume() {
        mCompositeDisposable = CompositeDisposable()

        when {
            chatId != 0L -> {
                mCompositeDisposable.add(HomeToWorkClient.getChatService().getChatMessageList(chatId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            cache.clear()
                            cache.addAll(it)
                            chatView.setItems(it)
                        }, {
                            loadingError(it as RetrofitException)
                        }, {

                        }, {
                            chatView.onLoading()
                        }))
            }
            recipientId != 0L -> {
                mCompositeDisposable.add(HomeToWorkClient.getChatService().newChat(recipientId)
                        .flatMap {
                            chatId = it.id.toLong()
                            return@flatMap HomeToWorkClient.getChatService().getChatMessageList(it.id.toLong())
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            cache.clear()
                            cache.addAll(it)
                            chatView.setItems(it)
                        }, {
                            loadingError(it as RetrofitException)
                        }, {

                        }, {
                            chatView.onLoading()
                        }))
            }
        }

    }

    override fun getMessageList(): ArrayList<Message> {
        return cache
    }

    override fun onNewMessageEvent(newMessageEvent: NewMessageEvent) {

        if (chatId == newMessageEvent.chatId) {

            val message = Message()
            message.messageText = newMessageEvent.text
            message.messageUser = Author(recipientId)

            chatView.onNewMessage(message)

        }

    }

    override fun onPause() {
        mCompositeDisposable.clear()
    }

    override fun sendMessage(message: Message) {
        mCompositeDisposable.add(HomeToWorkClient.getChatService().sendMessageToChat(chatId, message.text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn {
                    sendMessageError(it as RetrofitException)
                    null
                }
                .subscribe())
    }

    private fun loadingError(exception: RetrofitException) {

        val errorMessage = when (exception.kind) {
            RetrofitException.Kind.NETWORK -> "Nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "Impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "Errore sconosciuto"
        }

        chatView.onLoadingError(errorMessage)

    }

    private fun sendMessageError(exception: RetrofitException) {

        val errorMessage = when (exception.kind) {
            RetrofitException.Kind.NETWORK -> "Nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "Impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "Errore sconosciuto"
        }

        chatView.onMessageSentError(errorMessage)


    }

}