package it.gruppoinfor.home2work.domain.interfaces

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.ChatMessageEntity
import it.gruppoinfor.home2work.domain.entities.ChatEntity

interface ChatRepository {
    fun getChatMessageList(chatId: Long): Observable<List<ChatMessageEntity>>
    fun sendMessage(chatId: Long, message: String): Observable<ChatMessageEntity>
    fun newChat(userId: Long): Observable<ChatEntity>
    fun getChatList(): Observable<List<ChatEntity>>
}