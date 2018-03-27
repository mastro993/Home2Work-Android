package it.gruppoinfor.home2work.di.chat

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.chat.ChatVMFactory
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.common.mappers.MessageEntityMessageMapper
import it.gruppoinfor.home2work.common.mappers.MessageMessageEntityMapper
import it.gruppoinfor.home2work.common.user.LocalUserData
import it.gruppoinfor.home2work.domain.interfaces.ChatRepository
import it.gruppoinfor.home2work.domain.usecases.GetChatMessageList
import it.gruppoinfor.home2work.domain.usecases.NewChat
import it.gruppoinfor.home2work.domain.usecases.SendMessage

@Module
class ChatModule {

    @Provides
    fun provideNewChatUseCase(chatRepository: ChatRepository): NewChat {
        return NewChat(ASyncTransformer(), chatRepository)
    }

    @Provides
    fun provideSendMessageUseCase(chatRepository: ChatRepository): SendMessage {
        return SendMessage(ASyncTransformer(), chatRepository)
    }


    @Provides
    fun provideGetChatMessageListUseCase(chatRepository: ChatRepository): GetChatMessageList {
        return GetChatMessageList(ASyncTransformer(), chatRepository)
    }

    @Provides
    fun provideHomeVMFactory(newChat: NewChat, getChatMessageList: GetChatMessageList, sendMessage: SendMessage, messageMapper: MessageEntityMessageMapper, entityMapper: MessageMessageEntityMapper, localUserData: LocalUserData): ChatVMFactory {
        return ChatVMFactory(newChat, getChatMessageList, sendMessage, messageMapper, entityMapper, localUserData)
    }
}