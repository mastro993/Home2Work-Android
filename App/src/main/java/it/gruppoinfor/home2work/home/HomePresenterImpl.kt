package it.gruppoinfor.home2work.home

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import it.gruppoinfor.home2work.api.HomeToWorkClient

class HomePresenterImpl constructor(private val homeView: HomeView) : HomePresenter {

    private lateinit var mCompositeDisposable: CompositeDisposable

    override fun onResume() {

        mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable.add(HomeToWorkClient.getUserService().getChatList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn {
                    emptyList()
                }
                .subscribe({
                    homeView.refreshInboxCounter(it.count {it.unreadCnt > 0 })
                }))

    }

    override fun onPause() {

    }

    override fun onNewMessage() {

        mCompositeDisposable.add(HomeToWorkClient.getUserService().getChatList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn {
                    emptyList()
                }
                .subscribe({
                    homeView.refreshInboxCounter(it.count { it.unreadCnt > 0 })
                }))

    }
}