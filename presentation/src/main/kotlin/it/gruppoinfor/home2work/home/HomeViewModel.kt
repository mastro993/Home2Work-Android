package it.gruppoinfor.home2work.home

import android.arch.lifecycle.MutableLiveData
import it.gruppoinfor.home2work.common.BaseViewModel
import it.gruppoinfor.home2work.domain.usecases.GetChatList


class HomeViewModel(
        private val getChatList: GetChatList
) : BaseViewModel() {

    var viewState: MutableLiveData<HomeViewState> = MutableLiveData()

    init {
        viewState.value = HomeViewState()
    }

    fun getInboxCount() {
        addDisposable(getChatList.observable()
                .subscribe({

                    val inboxCount = it.count { it.unreadCnt > 0 }
                    val newViewState = viewState.value?.copy(inboxCount = inboxCount)
                    viewState.value = newViewState

                }, {

                }))
    }


}