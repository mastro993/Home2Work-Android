package it.gruppoinfor.home2work.di.chat

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.common.mappers.ChatEntityChatMapper
import it.gruppoinfor.home2work.domain.interfaces.ChatRepository
import it.gruppoinfor.home2work.domain.usecases.GetChatList
import it.gruppoinfor.home2work.chat.ChatVMFactory

@ChatScope
@Module
class ChatModule {

    @Provides
    fun provideGetChatListUseCase(chatRepository: ChatRepository): GetChatList {
        return GetChatList(ASyncTransformer(), chatRepository)
    }

    @Provides
    fun provideMainVMFactory(getChatList: GetChatList, mapper: ChatEntityChatMapper): ChatVMFactory {
        return ChatVMFactory(getChatList, mapper)
    }

}