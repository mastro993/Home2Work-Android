package it.gruppoinfor.home2work

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import it.gruppoinfor.home2work.common.user.LocalUserData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import it.gruppoinfor.home2work.domain.usecases.CreateShare
import it.gruppoinfor.home2work.domain.usecases.GetActiveShare
import it.gruppoinfor.home2work.domain.usecases.JoinShare
import it.gruppoinfor.home2work.entities.Share


class MainVMFactory(
        private val getActiveShare: GetActiveShare,
        private val joinShare: JoinShare,
        private val createShare: CreateShare,
        private val shareMapper: Mapper<ShareEntity, Share>,
        private val localUserData: LocalUserData
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(getActiveShare, joinShare, createShare, shareMapper, localUserData) as T
    }
}