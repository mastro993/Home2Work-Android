package it.gruppoinfor.home2work.match

import android.arch.lifecycle.MutableLiveData
import it.gruppoinfor.home2work.MainActivity
import it.gruppoinfor.home2work.common.BaseViewModel
import it.gruppoinfor.home2work.common.SingleLiveEvent
import it.gruppoinfor.home2work.common.events.BottomNavBadgeEvent
import it.gruppoinfor.home2work.common.extensions.getScore
import it.gruppoinfor.home2work.common.mappers.MatchMatchEntityMapper
import it.gruppoinfor.home2work.common.views.ScreenState
import it.gruppoinfor.home2work.data.api.RetrofitException
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.MatchEntity
import it.gruppoinfor.home2work.domain.usecases.EditMatch
import it.gruppoinfor.home2work.domain.usecases.GetMatch
import it.gruppoinfor.home2work.domain.usecases.GetMatchList
import it.gruppoinfor.home2work.entities.Match
import org.greenrobot.eventbus.EventBus


class MatchViewModel(
        private val getMatch: GetMatch,
        private val getMatchList: GetMatchList,
        private val editMatch: EditMatch,
        private val mapper: Mapper<MatchEntity, Match>
) : BaseViewModel() {

    var viewState: MutableLiveData<MatchViewState> = MutableLiveData()
    var errorState: SingleLiveEvent<String?> = SingleLiveEvent()

    init {
        viewState.value = MatchViewState()
    }

    fun getMatchList() {

        addDisposable(getMatchList.observable()
                .map {
                    it.map { mapper.mapFrom(it) }
                }
                .doOnSubscribe {

                    val newViewState = viewState.value?.copy(
                            screenState = ScreenState.Loading
                    )
                    viewState.value = newViewState

                }
                .subscribe({


                    val newViewState = when {
                        it.isEmpty() -> {

                            EventBus.getDefault().post(BottomNavBadgeEvent(MainActivity.MATCHES_TAB))

                            viewState.value?.copy(
                                    screenState = ScreenState.Empty("Non sono ancora disponibili match per te")
                            )

                        }
                        it.any { it.getScore() == null } -> {

                            EventBus.getDefault().post(BottomNavBadgeEvent(MainActivity.MATCHES_TAB))

                            viewState.value?.copy(
                                    screenState = ScreenState.Done,
                                    isLightMatches = true,
                                    matches = it)

                        }
                        else -> {

                            EventBus.getDefault().post(BottomNavBadgeEvent(MainActivity.MATCHES_TAB, it.count { it.isNew }.toString()))

                            viewState.value?.copy(
                                    screenState = ScreenState.Done,
                                    isLightMatches = false,
                                    matches = it)

                        }
                    }

                    viewState.value = newViewState

                }, {

                    if (it is RetrofitException) {

                        val errorMessage = when (it.kind) {
                            RetrofitException.Kind.NETWORK -> "Nessuna connessione ad internet"
                            RetrofitException.Kind.HTTP -> "Impossibile contattare il server"
                            RetrofitException.Kind.UNEXPECTED -> "Errore sconosciuto"
                        }

                        val newViewState = viewState.value?.copy(
                                screenState = ScreenState.Error(errorMessage)
                        )
                        viewState.value = newViewState

                    }


                }))


    }

    fun refreshMatchList() {
        addDisposable(getMatchList.observable()
                .map {
                    it.map { mapper.mapFrom(it) }
                }
                .doOnSubscribe {
                    val newViewState = viewState.value?.copy(
                            isRefreshing = true
                    )
                    viewState.value = newViewState
                }
                .subscribe({

                    val newViewState = when {
                        it.isEmpty() -> {

                            EventBus.getDefault().post(BottomNavBadgeEvent(MainActivity.MATCHES_TAB))

                            viewState.value?.copy(
                                    screenState = ScreenState.Empty("Non sono ancora disponibili match per te")
                            )

                        }
                        it.any { it.getScore() == null } -> {

                            EventBus.getDefault().post(BottomNavBadgeEvent(MainActivity.MATCHES_TAB))

                            viewState.value?.copy(
                                    isRefreshing = false,
                                    isLightMatches = true,
                                    matches = it)

                        }
                        else -> {

                            EventBus.getDefault().post(BottomNavBadgeEvent(MainActivity.MATCHES_TAB, it.count { it.isNew }.toString()))

                            viewState.value?.copy(
                                    isRefreshing = false,
                                    isLightMatches = false,
                                    matches = it)

                        }
                    }

                    viewState.value = newViewState

                }, {


                    if (it is RetrofitException) {

                        errorState.value = when (it.kind) {
                            RetrofitException.Kind.NETWORK -> "Nessuna connessione ad internet"
                            RetrofitException.Kind.HTTP -> "Impossibile contattare il server"
                            RetrofitException.Kind.UNEXPECTED -> "Errore sconosciuto"
                        }

                        val newViewState = viewState.value?.copy(
                                isRefreshing = false
                        )
                        viewState.value = newViewState

                    }

                }))
    }

    fun getMatch(matchId: Long) {
        addDisposable(getMatch.getById(matchId)
                .map {
                    mapper.mapOptional(it)
                }
                .subscribe({

                }, {

                }))
    }

    fun setMatchAsViewed(match: Match) {
        val matchEntity = MatchMatchEntityMapper().mapFrom(match)
        matchEntity.isNew = false
        addDisposable(editMatch.edit(matchEntity).subscribe(
                {

                }, {

        }))


    }

    fun hideMatch(match: Match) {
        val matchEntity = MatchMatchEntityMapper().mapFrom(match)
        matchEntity.isHidden = true
        addDisposable(editMatch.edit(matchEntity).subscribe(
                {

                }, {

        }))
    }


}