package it.gruppoinfor.home2work.di.sharecurrent

import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.common.mappers.GuestEntityGuestMapper
import it.gruppoinfor.home2work.common.mappers.ShareEntityShareMapper
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository
import it.gruppoinfor.home2work.domain.usecases.*
import it.gruppoinfor.home2work.sharecurrent.CurrentShareVMFactory


@Module
class CurrentShareModule {

    @Provides
    @CurrentShareScope
    fun provideGetActiveShareUseCase(shareRepository: ShareRepository): GetActiveShare {
        return GetActiveShare(ASyncTransformer(), shareRepository)
    }

    @Provides
    @CurrentShareScope
    fun provideGetShareGuestsUseCase(shareRepository: ShareRepository): GetShareGuests {
        return GetShareGuests(ASyncTransformer(), shareRepository)
    }

    @Provides
    @CurrentShareScope
    fun provideBanUserFromShareUseCase(shareRepository: ShareRepository): BanUserFromShare {
        return BanUserFromShare(ASyncTransformer(), shareRepository)
    }

    @Provides
    @CurrentShareScope
    fun provideCancelCurrentShareUseCase(shareRepository: ShareRepository): CancelCurrentShare {
        return CancelCurrentShare(ASyncTransformer(), shareRepository)
    }

    @Provides
    @CurrentShareScope
    fun provideLeaveShareUseCase(shareRepository: ShareRepository): LeaveShare {
        return LeaveShare(ASyncTransformer(), shareRepository)
    }

    @Provides
    @CurrentShareScope
    fun provideCompleteCurrentShareUseCase(shareRepository: ShareRepository): CompleteCurrentShare {
        return CompleteCurrentShare(ASyncTransformer(), shareRepository)
    }

    @Provides
    @CurrentShareScope
    fun provideFinishCurrentShareUseCase(shareRepository: ShareRepository): FinishCurrentShare {
        return FinishCurrentShare(ASyncTransformer(), shareRepository)
    }

    @Provides
    @CurrentShareScope
    fun provideCurrentShareVMFactory(
            getActiveShare: GetActiveShare,
            banUserFromShare: BanUserFromShare,
            cancelCurrentShare: CancelCurrentShare,
            leaveShare: LeaveShare,
            completeCurrentShare: CompleteCurrentShare,
            finishCurrentShare: FinishCurrentShare,
            shareMapper: ShareEntityShareMapper
    ): CurrentShareVMFactory {
        return CurrentShareVMFactory(
                getActiveShare,
                banUserFromShare,
                cancelCurrentShare,
                leaveShare,
                completeCurrentShare,
                finishCurrentShare,
                shareMapper
        )
    }

}