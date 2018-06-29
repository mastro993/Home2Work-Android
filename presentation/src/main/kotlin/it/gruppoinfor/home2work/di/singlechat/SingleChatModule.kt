package it.gruppoinfor.home2work.di.singlechat

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.chat.SingleChatVMFactory
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.common.mappers.MessageEntityMessageMapper
import it.gruppoinfor.home2work.common.mappers.MessageMessageEntityMapper
import it.gruppoinfor.home2work.common.LocalUserData
import it.gruppoinfor.home2work.domain.interfaces.ChatRepository
import it.gruppoinfor.home2work.domain.usecases.GetChatMessage
import it.gruppoinfor.home2work.domain.usecases.GetChatMessageList
import it.gruppoinfor.home2work.domain.usecases.NewChat
import it.gruppoinfor.home2work.domain.usecases.SendMessage

@Module
class SingleChatModule {

    @Provides
    @SingleChatScope
    fun provideNewChatUseCase(chatRepository: ChatRepository): NewChat {
        return NewChat(ASyncTransformer(), chatRepository)
    }

    @Provides
    @SingleChatScope
    fun provideGetMessageUseCase(chatRepository: ChatRepository): GetChatMessage {
        return GetChatMessage(ASyncTransformer(), chatRepository)
    }

    @Provides
    @SingleChatScope
    fun provideSendMessageUseCase(chatRepository: ChatRepository): SendMessage {
        return SendMessage(ASyncTransformer(), chatRepository)
    }


    @Provides
    @SingleChatScope
    fun provideGetChatMessageListUseCase(chatRepository: ChatRepository): GetChatMessageList {
        return GetChatMessageList(ASyncTransformer(), chatRepository)
    }

    @Provides
    @SingleChatScope
    fun provideHomeVMFactory(newChat: NewChat, getChatMessageList: GetChatMessageList, getChatMessage: GetChatMessage, sendMessage: SendMessage, messageMapper: MessageEntityMessageMapper, entityMapper: MessageMessageEntityMapper, localUserData: LocalUserData): SingleChatVMFactory {
        return SingleChatVMFactory(newChat, getChatMessageList, getChatMessage, sendMessage, messageMapper, entityMapper, localUserData)
    }
}