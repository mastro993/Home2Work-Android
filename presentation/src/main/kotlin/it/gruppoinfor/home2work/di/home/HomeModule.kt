package it.gruppoinfor.home2work.di.home

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.domain.interfaces.ChatRepository
import it.gruppoinfor.home2work.domain.usecases.GetChatList
import it.gruppoinfor.home2work.home.HomeVMFactory


@Module
class HomeModule {

    @Provides
    fun provideGetChatListUseCase(chatRepository: ChatRepository): GetChatList {
        return GetChatList(ASyncTransformer(), chatRepository)
    }

    @Provides
    fun provideHomeVMFactory(getChatList: GetChatList): HomeVMFactory {
        return HomeVMFactory(getChatList)
    }


}