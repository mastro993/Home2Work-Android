package it.gruppoinfor.home2work.di.inbox

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.common.mappers.ChatEntityChatMapper
import it.gruppoinfor.home2work.domain.interfaces.ChatRepository
import it.gruppoinfor.home2work.domain.usecases.GetChatList
import it.gruppoinfor.home2work.inbox.InboxVMFactory

@InboxScope
@Module
class InboxModule {

    @Provides
    fun provideGetChatListUseCase(chatRepository: ChatRepository): GetChatList {
        return GetChatList(ASyncTransformer(), chatRepository)
    }

    @Provides
    fun provideMainVMFactory(getChatList: GetChatList, mapper: ChatEntityChatMapper): InboxVMFactory {
        return InboxVMFactory(getChatList, mapper)
    }

}