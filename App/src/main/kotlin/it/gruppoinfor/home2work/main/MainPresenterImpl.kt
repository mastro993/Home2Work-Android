package it.gruppoinfor.home2work.main

import android.location.Location
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import it.gruppoinfor.home2work.api.HomeToWorkClient
import it.gruppoinfor.home2work.api.RetrofitException
import it.gruppoinfor.home2work.location.LatLng
import timber.log.Timber

class MainPresenterImpl constructor(val mainView: MainView) : MainPresenter {

    private lateinit var mCompositeDisposable: CompositeDisposable

    override fun onResume() {
        mCompositeDisposable = CompositeDisposable()

        getOngoingShare()

    }

    override fun newShare() {
        mCompositeDisposable.add(HomeToWorkClient.getShareService().createNewShare()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn {
                    HomeToWorkClient.ongoingShare = null
                    null
                }
                .subscribe({
                    HomeToWorkClient.ongoingShare = it
                    mainView.onShareCreated()
                }, {
                    creationError(it as RetrofitException)
                }))
    }

    override fun joinShare(shareId: Long, hostLatLng: LatLng, joinLocation: Location) {

        if (hostLatLng.distanceTo(joinLocation) > 1000) {
            mainView.showError("Codice non valido")
        } else {
            val locationString = joinLocation.latitude.toString() + "," + joinLocation.longitude
            mCompositeDisposable.add(HomeToWorkClient.getShareService().joinShare(shareId, locationString)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn {
                        null
                    }
                    .subscribe({
                        HomeToWorkClient.ongoingShare = it
                        mainView.onShareJoined()
                    }, {
                        joinError(it as RetrofitException)
                    }))
        }
    }

    override fun getOngoingShare() {
        mCompositeDisposable.add(HomeToWorkClient.getShareService().getOngoingShare()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn {
                    HomeToWorkClient.ongoingShare = null
                    null
                }
                .doFinally {
                    mainView.onOngoingShareRefresh()
                }
                .subscribe({
                    HomeToWorkClient.ongoingShare = it
                    mainView.onOngoingShareRefresh()
                }, {
                    Timber.e(it)
                }))
    }

    override fun onPause() {
        mCompositeDisposable.clear()
    }

    private fun creationError(exception: RetrofitException) {

        val errorMessage = "Impossibile creare una nuova condivisione: " + when (exception.kind) {
            RetrofitException.Kind.NETWORK -> "nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "errore sconosciuto"
        }

        mainView.showError(errorMessage)

    }

    private fun joinError(exception: RetrofitException) {

        val errorMessage = "Impossibile unirsi alla condivisione: " + when (exception.kind) {
            RetrofitException.Kind.NETWORK -> "nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "errore sconosciuto"
        }

        mainView.showError(errorMessage)

    }
}