package it.gruppoinfor.home2work.home

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import it.gruppoinfor.home2work.domain.usecases.GetChatList


class HomeVMFactory(
        private val getChatList: GetChatList
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HomeViewModel(getChatList) as T
    }
}