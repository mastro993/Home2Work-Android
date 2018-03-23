package it.gruppoinfor.home2work.sharehistory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import it.gruppoinfor.home2work.domain.usecases.GetShareList
import it.gruppoinfor.home2work.entities.Share


class ShareHistoryVMFactory(
        private val getShareList: GetShareList,
        private val mapper: Mapper<ShareEntity, Share>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ShareHistoryViewModel(getShareList, mapper) as T
    }
}