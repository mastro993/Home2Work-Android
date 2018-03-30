package it.gruppoinfor.home2work.home

import android.arch.lifecycle.MutableLiveData
import it.gruppoinfor.home2work.MainActivity
import it.gruppoinfor.home2work.common.BaseViewModel
import it.gruppoinfor.home2work.common.events.BottomNavBadgeEvent
import it.gruppoinfor.home2work.domain.usecases.GetChatList
import org.greenrobot.eventbus.EventBus


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

                    EventBus.getDefault().post(BottomNavBadgeEvent(MainActivity.HOME_TAB, if (inboxCount == 0) "" else inboxCount.toString()))

                    val newViewState = viewState.value?.copy(inboxCount = inboxCount)
                    viewState.value = newViewState

                }, {
                    it.printStackTrace()
                }))

    }


}