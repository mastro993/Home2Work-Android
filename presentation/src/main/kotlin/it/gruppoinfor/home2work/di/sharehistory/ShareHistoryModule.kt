package it.gruppoinfor.home2work.di.sharehistory

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository
import it.gruppoinfor.home2work.domain.interfaces.UserRepository
import it.gruppoinfor.home2work.domain.usecases.GetShareList
import it.gruppoinfor.home2work.domain.usecases.UserLogin
import it.gruppoinfor.home2work.mappers.ShareEntityShareMapper
import it.gruppoinfor.home2work.sharehistory.ShareHistoryVMFactory

@ShareHistoryScope
@Module
class ShareHistoryModule {

    @Provides
    fun provideGetShareListUseCase(shareRepository: ShareRepository): GetShareList {
        return GetShareList(ASyncTransformer(), shareRepository)
    }


    @Provides
    fun provideAuthVMFactory(getShareList: GetShareList, shareEntityShareMapper: ShareEntityShareMapper): ShareHistoryVMFactory {
        return ShareHistoryVMFactory(getShareList, shareEntityShareMapper)
    }

}