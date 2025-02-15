package it.gruppoinfor.home2work.chat

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import it.gruppoinfor.home2work.common.LocalUserData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ChatMessageEntity
import it.gruppoinfor.home2work.domain.usecases.GetChatMessage
import it.gruppoinfor.home2work.domain.usecases.GetChatMessageList
import it.gruppoinfor.home2work.domain.usecases.NewChat
import it.gruppoinfor.home2work.domain.usecases.SendMessage
import it.gruppoinfor.home2work.entities.ChatMessage


class SingleChatVMFactory(
        private var newChat: NewChat,
        private var getChatMessageList: GetChatMessageList,
        private var getMessage: GetChatMessage,
        private var sendMessage: SendMessage,
        private var messageMapper: Mapper<ChatMessageEntity, ChatMessage>,
        private var entityMapper: Mapper<ChatMessage, ChatMessageEntity>,
        private var localUserData: LocalUserData
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SingleChatViewModel(newChat, getChatMessageList, getMessage, sendMessage, messageMapper, entityMapper, localUserData) as T
    }
}