package it.gruppoinfor.home2work.di.sharehistory

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.common.mappers.ShareEntityShareMapper
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository
import it.gruppoinfor.home2work.domain.usecases.GetShareList
import it.gruppoinfor.home2work.sharehistory.ShareHistoryVMFactory

@Module
class ShareHistoryModule {

    @Provides
    @ShareHistoryScope
    fun provideGetShareListUseCase(shareRepository: ShareRepository): GetShareList {
        return GetShareList(ASyncTransformer(), shareRepository)
    }


    @Provides
    @ShareHistoryScope
    fun provideAuthVMFactory(getShareList: GetShareList, shareEntityShareMapper: ShareEntityShareMapper): ShareHistoryVMFactory {
        return ShareHistoryVMFactory(getShareList, shareEntityShareMapper)
    }

}