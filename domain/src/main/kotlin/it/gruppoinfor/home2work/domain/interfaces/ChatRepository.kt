package it.gruppoinfor.home2work.domain.interfaces

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.ChatEntity
import it.gruppoinfor.home2work.domain.entities.ChatMessageEntity

interface ChatRepository {
    fun getChatMessageList(chatId: Long): Observable<List<ChatMessageEntity>>
    fun getChatMessage(messageId: Long): Observable<ChatMessageEntity>
    fun sendMessage(chatId: Long, message: String): Observable<ChatMessageEntity>
    fun newChat(userId: Long): Observable<ChatEntity>
    fun getChatList(): Observable<List<ChatEntity>>
}