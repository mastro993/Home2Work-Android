package it.gruppoinfor.home2work.sharecurrent

import android.graphics.Bitmap
import android.location.Location
import io.reactivex.disposables.CompositeDisposable
import it.gruppoinfor.home2work.data.api.RetrofitException

class OngoingSharePresenterImpl constructor(private val ongoingShareView: OngoingShareView) : OngoingSharePresenter {

    private lateinit var mCompositeDisposable: CompositeDisposable

    override fun onResume() {
        mCompositeDisposable = CompositeDisposable()
        refreshGuests()
    }

    override fun onPause() {
        mCompositeDisposable.clear()
    }

    override fun refreshGuests() {
       /* mCompositeDisposable.add(HomeToWorkClient.getShareService().getOngoingShare()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn {
                    null
                }.subscribe({
                    HomeToWorkClient.ongoingShare = it
                    ongoingShareView.setGuests(it)
                }, {
                    refreshError(it as RetrofitException)
                }))*/
    }

    private fun refreshError(retrofitException: RetrofitException) {
        /*val errorMessage = "Impossibile aggiornate gli ospiti della condivisione: " + when (retrofitException.kind) {
            RetrofitException.Kind.NETWORK -> "nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "errore sconosciuto"
        }

        ongoingShareView.showErrorMessage(errorMessage)*/
    }

    override fun banGuest(guestId: Long) {
       /* val shareId = HomeToWorkClient.ongoingShare?.id!!
        mCompositeDisposable.add(HomeToWorkClient.getShareService().banGuestFromShare(shareId, guestId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn {
                    null
                }.subscribe({
                    ongoingShareView.onGuestBanned()
                }, {
                    banError(it as RetrofitException)
                }))*/
    }

    private fun banError(retrofitException: RetrofitException) {
        val errorMessage = "Impossibile espellere l'ospite dalla condivisione: " + when (retrofitException.kind) {
            RetrofitException.Kind.NETWORK -> "nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "errore sconosciuto"
        }

        ongoingShareView.showErrorMessage(errorMessage)
    }

    override fun cancelOngoingShare() {
        /*val shareId = HomeToWorkClient.ongoingShare?.id!!
        mCompositeDisposable.add(HomeToWorkClient.getShareService().cancelShare(shareId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn {
                    null
                }.subscribe({
                    ongoingShareView.onShareCanceled()
                }, {
                    cancelError(it as RetrofitException)
                }))*/
    }

    private fun cancelError(retrofitException: RetrofitException) {
        val errorMessage = "Impossibile interrompere la condivisione: " + when (retrofitException.kind) {
            RetrofitException.Kind.NETWORK -> "nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "errore sconosciuto"
        }

        ongoingShareView.showErrorMessage(errorMessage)
    }

    override fun leaveOngoingShare() {
        /*val shareId = HomeToWorkClient.ongoingShare?.id!!
        mCompositeDisposable.add(HomeToWorkClient.getShareService().leaveShare(shareId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn {
                    null
                }.subscribe({
                    HomeToWorkClient.ongoingShare = null
                    ongoingShareView.onShareCanceled()
                }, {
                    leaveError(it as RetrofitException)
                }))*/
    }

    private fun leaveError(retrofitException: RetrofitException) {
        val errorMessage = "Impossibile abbandonare la condivisione: " + when (retrofitException.kind) {
            RetrofitException.Kind.NETWORK -> "nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "errore sconosciuto"
        }

        ongoingShareView.showErrorMessage(errorMessage)
    }

    override fun getShareQRCode(joinLocation: Location): Bitmap? {
        /*val locationString = joinLocation.latitude.toString() + "," + joinLocation.longitude
        val shareId = HomeToWorkClient.ongoingShare?.id!!
        return try {
            QREncoder.encodeText("$shareId,$locationString")
        } catch (e: Exception) {
            ongoingShareView.showErrorMessage("Impossibile generare codice condivisione")
            null
        }*/
        return null
    }

    override fun finishShare() {
        /*val shareId = HomeToWorkClient.ongoingShare?.id!!
        mCompositeDisposable.add(HomeToWorkClient.getShareService().finishShare(shareId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn {
                    null
                }.subscribe({
                    ongoingShareView.onShareCanceled()
                }, {
                    finishError(it as RetrofitException)
                }))*/
    }

    override fun completeShare(hostLocation: Location, endLocation: Location) {

        /*if (hostLatLng.distanceTo(endLocation) > 1000) {
            ongoingShareView.showErrorMessage("Codice non valido.")
        } else {
            val shareId = HomeToWorkClient.ongoingShare?.id!!
            val locationString = endLocation.latitude.toString() + "," + endLocation.longitude
            mCompositeDisposable.add(HomeToWorkClient.getShareService().completeShare(shareId, locationString)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn {
                        null
                    }.subscribe({
                        ongoingShareView.onShareCompleted()
                    }, {
                        finishError(it as RetrofitException)
                    }))
        }*/

    }

    private fun finishError(retrofitException: RetrofitException) {
        val errorMessage = "Impossibile completare la condivisione: " + when (retrofitException.kind) {
            RetrofitException.Kind.NETWORK -> "nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "errore sconosciuto"
        }

        ongoingShareView.showErrorMessage(errorMessage)
    }


}