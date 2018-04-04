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
@CurrentShareScope
class CurrentShareModule {

    @Provides
    fun provideGetActiveShareUseCase(shareRepository: ShareRepository): GetActiveShare {
        return GetActiveShare(ASyncTransformer(), shareRepository)
    }

    @Provides
    fun provideGetShareGuestsUseCase(shareRepository: ShareRepository): GetShareGuests {
        return GetShareGuests(ASyncTransformer(), shareRepository)
    }

    @Provides
    fun provideBanUserFromShareUseCase(shareRepository: ShareRepository): BanUserFromShare {
        return BanUserFromShare(ASyncTransformer(), shareRepository)
    }

    @Provides
    fun provideCancelCurrentShareUseCase(shareRepository: ShareRepository): CancelCurrentShare {
        return CancelCurrentShare(ASyncTransformer(), shareRepository)
    }

    @Provides
    fun provideLeaveShareUseCase(shareRepository: ShareRepository): LeaveShare {
        return LeaveShare(ASyncTransformer(), shareRepository)
    }

    @Provides
    fun provideCompleteCurrentShareUseCase(shareRepository: ShareRepository): CompleteCurrentShare {
        return CompleteCurrentShare(ASyncTransformer(), shareRepository)
    }

    @Provides
    fun provideFinishCurrentShareUseCase(shareRepository: ShareRepository): FinishCurrentShare {
        return FinishCurrentShare(ASyncTransformer(), shareRepository)
    }

    @Provides
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