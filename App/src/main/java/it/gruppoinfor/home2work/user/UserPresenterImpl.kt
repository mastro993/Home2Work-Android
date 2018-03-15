package it.gruppoinfor.home2work.user

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import it.gruppoinfor.home2work.api.HomeToWorkClient
import it.gruppoinfor.home2work.api.RetrofitException


class UserPresenterImpl constructor(private val userView: UserView, private val userId: Long) : UserPresenter {

    private lateinit var mCompositeDisposable: CompositeDisposable

    private var userProfile: UserProfile? = null

    override fun onCreate() {

        mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable.add(HomeToWorkClient.getUserService().getUserProfileById(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    userView.onLoading()
                }
                .doOnError {
                    loadingError(it as RetrofitException)
                    userProfile
                }
                .subscribe {
                    userProfile = it
                    userView.setProfileData(it)
                })

    }

    override fun onRefresh() {
        mCompositeDisposable.add(HomeToWorkClient.getUserService().getUserProfileById(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    userView.onRefresh()
                }
                .doOnError {
                    refreshError(it as RetrofitException)
                    userProfile
                }
                .doFinally {
                    userView.onRefreshDone()
                }
                .subscribe {
                    userProfile = it
                    userView.setProfileData(it)
                })
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

        userView.onLoadingError(errorMessage)

    }

    private fun refreshError(exception: RetrofitException) {

        val errorMessage = "Impossibile aggiornare il profilo. " + when (exception.kind) {
            RetrofitException.Kind.NETWORK -> "Nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "Impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "Errore sconosciuto"
        }

        userView.showErrorMessage(errorMessage)

    }

}