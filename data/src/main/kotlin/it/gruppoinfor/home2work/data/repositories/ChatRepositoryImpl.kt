package it.gruppoinfor.home2work.data.repositories

import io.reactivex.Observable
import it.gruppoinfor.home2work.data.api.APIService
import it.gruppoinfor.home2work.data.api.get
import it.gruppoinfor.home2work.data.api.services.ChatService
import it.gruppoinfor.home2work.data.mappers.ChatDataEntityMapper
import it.gruppoinfor.home2work.data.mappers.ChatMessageDataEntityMapper
import it.gruppoinfor.home2work.domain.entities.ChatEntity
import it.gruppoinfor.home2work.domain.entities.ChatMessageEntity
import it.gruppoinfor.home2work.domain.interfaces.ChatRepository

class ChatRepositoryImpl(
        private val chatMapper: ChatDataEntityMapper,
        private val chatMessageMapper: ChatMessageDataEntityMapper
) : ChatRepository {

    private val chatService = APIService.get<ChatService>()

    override fun getChatMessageList(chatId: Long): Observable<List<ChatMessageEntity>> {
        return chatService.getChatMessageList(chatId).map {
            it.map {
                chatMessageMapper.mapFrom(it)
            }
        }
    }

    override fun getChatMessage(messageId: Long): Observable<ChatMessageEntity> {
        return chatService.getMessageFromChat(messageId).map {
            chatMessageMapper.mapFrom(it)
        }
    }

    override fun sendMessage(chatId: Long, message: String): Observable<ChatMessageEntity> {
        return chatService.sendMessageToChat(chatId, message)
                .map {
                    chatMessageMapper.mapFrom(it)
                }
    }

    override fun newChat(userId: Long): Observable<ChatEntity> {
        return chatService.newChat(userId)
                .map {
                    chatMapper.mapFrom(it)
                }
    }

    override fun getChatList(): Observable<List<ChatEntity>> {
        return chatService.getChatList().map {
            it.map {
                chatMapper.mapFrom(it)
            }
        }
    }
}