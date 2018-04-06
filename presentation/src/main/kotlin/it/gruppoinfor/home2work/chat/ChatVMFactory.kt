package it.gruppoinfor.home2work.chat

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ChatEntity
import it.gruppoinfor.home2work.domain.usecases.GetChatList
import it.gruppoinfor.home2work.entities.Chat


class ChatVMFactory(
        private val getChatList: GetChatList,
        private val mapper: Mapper<ChatEntity, Chat>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ChatViewModel(getChatList, mapper) as T
    }
}