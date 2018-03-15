package it.gruppoinfor.home2work.match

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import it.gruppoinfor.home2work.api.HomeToWorkClient
import it.gruppoinfor.home2work.api.RetrofitException


class MatchesPresenterImpl constructor(private val matchesView: MatchesView) : MatchesPresenter {

    private lateinit var mCompositeDisposable: CompositeDisposable

    override fun onViewCreated() {
        mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable.add(HomeToWorkClient.getUserService().getMatchList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { matchesView.onLoading() }
                .subscribe({

                    if (it.isEmpty()) {
                        matchesView.onEmptyList()
                        refreshBadgeCounter(0)
                    } else {
                        matchesView.setMatches(it)
                        refreshBadgeCounter(it.count { it.isNew })
                    }

                }, {
                    loadingError(it as RetrofitException)
                }))
    }

    override fun onRefresh() {
        mCompositeDisposable.add(HomeToWorkClient.getUserService().getMatchList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { matchesView.onRefreshDone() }
                .subscribe({

                    if (it.isEmpty()) {
                        matchesView.onEmptyList()
                        refreshBadgeCounter(0)
                    } else {
                        matchesView.setMatches(it)
                        refreshBadgeCounter(it.count { it.isNew })
                    }

                }, {
                    refreshError(it as RetrofitException)
                }, {
                    matchesView.onRefreshDone()
                }, {
                    matchesView.onRefresh()
                }))
    }

    override fun onPause() {
        mCompositeDisposable.clear()
    }

    override fun hideMatch(match: Match) {

        match.isHidden = true
        match.isNew = false

        mCompositeDisposable.add(
                HomeToWorkClient.getMatchService().editMatch(match)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            //..
                        }, {
                            //..
                        })

        )

    }

    override fun setMatchAsViewed(match: Match) {

        match.isNew = false

        mCompositeDisposable.add(
                HomeToWorkClient.getMatchService().editMatch(match)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            //..
                        }, {
                            //..
                        })

        )
    }

    private fun hideError(exception: RetrofitException) {
        val errorMessage = "Impossibile nascondere match: " + when (exception.kind) {
            RetrofitException.Kind.NETWORK -> "nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "errore sconosciuto"
        }

        matchesView.showErrorMessage(errorMessage)
    }

    private fun refreshError(exception: RetrofitException) {
        val errorMessage = "Impossibile aggiornare la lista dei match: " + when (exception.kind) {
            RetrofitException.Kind.NETWORK -> "nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "errore sconosciuto"
        }

        matchesView.showErrorMessage(errorMessage)
    }

    private fun loadingError(exception: RetrofitException) {
        val errorMessage = when (exception.kind) {
            RetrofitException.Kind.NETWORK -> "Nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "Impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "Errore sconosciuto"
        }

        matchesView.onLoadingError(errorMessage)
    }

    private fun refreshBadgeCounter(newMatches: Int) {
        val badge = if (newMatches > 0) newMatches.toString() else ""
        matchesView.onBadgeRefresh(badge)
    }
}