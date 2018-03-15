package it.gruppoinfor.home2work.chat

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import it.gruppoinfor.home2work.api.HomeToWorkClient
import it.gruppoinfor.home2work.api.RetrofitException


class InboxPresenterImpl constructor(private val inboxView: InboxView) : InboxPresenter {

    private lateinit var mCompositeDisposable: CompositeDisposable

    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable.add(HomeToWorkClient.getUserService().getChatList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    inboxView.setItems(it)
                }, {
                    loadingError(it as RetrofitException)
                }, {
                    //...
                }, {
                    inboxView.onLoading()
                }))
    }

    override fun onResume() {
        mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable.add(HomeToWorkClient.getUserService().getChatList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    inboxView.setItems(it)
                }, {
                    loadingError(it as RetrofitException)
                }))
    }

    override fun onRefresh() {
        mCompositeDisposable.add(HomeToWorkClient.getUserService().getChatList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { inboxView.onRefreshComplete() }
                .subscribe({
                    inboxView.setItems(it)
                }, {
                    refreshError(it as RetrofitException)
                }, {
                    inboxView.onRefreshComplete()
                }, {
                    inboxView.onRefresh()
                }))
    }

    override fun onMessage() {
        mCompositeDisposable.add(HomeToWorkClient.getUserService().getChatList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    inboxView.setItems(it)
                }, {
                    // ...
                }))
    }

    override fun onPause() {
        mCompositeDisposable.clear()
    }

    private fun loadingError(exception: RetrofitException) {
        val errorMessage = when (exception.kind) {
            RetrofitException.Kind.NETWORK -> "Nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "Impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "Errore sconosciuto"
        }

        inboxView.onLoadingError(errorMessage)
    }

    private fun refreshError(exception: RetrofitException) {
        val errorMessage = "Impossibile aggiornare la lista delle conversazioni." + when (exception.kind) {
            RetrofitException.Kind.NETWORK -> "Nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "Impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "Errore sconosciuto"
        }

        inboxView.showErrorMessage(errorMessage)
    }


}