package it.gruppoinfor.home2work.share

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import it.gruppoinfor.home2work.api.HomeToWorkClient
import it.gruppoinfor.home2work.api.RetrofitException


class SharesPresenterImpl constructor(private val sharesView: SharesView) : SharesPresenter {


    private lateinit var mCompositeDisposable: CompositeDisposable

    override fun onResume() {
        mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable.add(HomeToWorkClient.getUserService().getShareList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    sharesView.setItems(it)
                }, {
                    loadingError(it as RetrofitException)
                }, {

                }, {
                    sharesView.onLoading()
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

        sharesView.onLoadingError(errorMessage)

    }
}