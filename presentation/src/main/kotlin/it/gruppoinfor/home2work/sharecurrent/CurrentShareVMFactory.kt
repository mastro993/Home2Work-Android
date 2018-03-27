package it.gruppoinfor.home2work.sharecurrent

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import it.gruppoinfor.home2work.domain.usecases.*
import it.gruppoinfor.home2work.entities.Share


class CurrentShareVMFactory(
        private val getActiveShare: GetActiveShare,
        private val banUserFromShare: BanUserFromShare,
        private val cancelCurrentShare: CancelCurrentShare,
        private val leaveShare: LeaveShare,
        private val completeCurrentShare: CompleteCurrentShare,
        private val finishCurrentShare: FinishCurrentShare,
        private val shareMapper: Mapper<ShareEntity, Share>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CurrentShareViewModel(getActiveShare, banUserFromShare, cancelCurrentShare, leaveShare, completeCurrentShare, finishCurrentShare, shareMapper) as T
    }
}