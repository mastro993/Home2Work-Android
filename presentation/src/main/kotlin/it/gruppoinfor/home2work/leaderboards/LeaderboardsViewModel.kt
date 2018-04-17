package it.gruppoinfor.home2work.leaderboards

import android.arch.lifecycle.MutableLiveData
import it.gruppoinfor.home2work.common.BaseViewModel
import it.gruppoinfor.home2work.common.SingleLiveEvent
import it.gruppoinfor.home2work.common.views.ScreenState
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.LeaderboardEntity
import it.gruppoinfor.home2work.domain.entities.UserRankingEntity
import it.gruppoinfor.home2work.domain.usecases.GetUserLeaderboards
import it.gruppoinfor.home2work.entities.Leaderboard
import it.gruppoinfor.home2work.entities.UserRanking

class LeaderboardsViewModel(
        private val getUserLeaderboards: GetUserLeaderboards,
        private val userRankingMapper: Mapper<UserRankingEntity, UserRanking>
) : BaseViewModel() {

    var viewState: MutableLiveData<LeaderboardsViewState> = MutableLiveData()
    var errorState: SingleLiveEvent<String> = SingleLiveEvent()
    var loadingState: SingleLiveEvent<Boolean> = SingleLiveEvent()
    var newLeaderboardPage: SingleLiveEvent<List<UserRanking>> = SingleLiveEvent()

    init {
        viewState.value = LeaderboardsViewState()
    }

    fun getLeaderboard(type: Leaderboard.Type?, range: Leaderboard.Range?, timespan: Leaderboard.TimeSpan?, companyId: Long? = null, pageSize: Int?, page: Int?) {

        val typeEntity = type?.let { LeaderboardEntity.Type.valueOf(it.toString()) }
        val rangeEntity = range?.let { LeaderboardEntity.Range.valueOf(it.toString()) }
        val timespanEntity = timespan?.let { LeaderboardEntity.TimeSpan.valueOf(it.toString()) }

        addDisposable(getUserLeaderboards.get(typeEntity, rangeEntity, timespanEntity, companyId, page, pageSize)
                .map { it.map { userRankingMapper.mapFrom(it) } }
                .doOnSubscribe {
                    val newViewState = viewState.value?.copy(
                            screenState = ScreenState.Loading
                    )

                    viewState.value = newViewState
                }
                .subscribe({

                    val newViewState = viewState.value?.copy(
                            screenState = ScreenState.Done,
                            leaderboard = it
                    )

                    viewState.value = newViewState


                }, {

                    val newViewState = viewState.value?.copy(
                            screenState = ScreenState.Error("Impossibile ottenere la classifica al momento")
                    )

                    viewState.value = newViewState

                }))

    }

    fun refreshLeaderboard(type: Leaderboard.Type?, range: Leaderboard.Range?, timespan: Leaderboard.TimeSpan?, companyId: Long? = null, pageSize: Int?, page: Int?) {

        val typeEntity = type?.let { LeaderboardEntity.Type.valueOf(it.toString()) }
        val rangeEntity = range?.let { LeaderboardEntity.Range.valueOf(it.toString()) }
        val timespanEntity = timespan?.let { LeaderboardEntity.TimeSpan.valueOf(it.toString()) }

        addDisposable(getUserLeaderboards.get(typeEntity, rangeEntity, timespanEntity, companyId, page, pageSize)
                .map { it.map { userRankingMapper.mapFrom(it) } }
                .doOnSubscribe {
                    val newViewState = viewState.value?.copy(
                            isRefreshing = true
                    )
                    viewState.value = newViewState
                }
                .subscribe({

                    val newViewState = viewState.value?.copy(
                            isRefreshing = false,
                            leaderboard = it
                    )
                    viewState.value = newViewState

                }, {

                    val newViewState = viewState.value?.copy(
                            isRefreshing = false
                    )
                    viewState.value = newViewState

                    errorState.value = "Impossibile aggiornare la classifica al momento"

                }))

    }

    fun getLeaderboardNewPage(type: Leaderboard.Type?, range: Leaderboard.Range?, timespan: Leaderboard.TimeSpan?, companyId: Long? = null, pageSize: Int?, page: Int?) {

        val typeEntity = type?.let { LeaderboardEntity.Type.valueOf(it.toString()) }
        val rangeEntity = range?.let { LeaderboardEntity.Range.valueOf(it.toString()) }
        val timespanEntity = timespan?.let { LeaderboardEntity.TimeSpan.valueOf(it.toString()) }

        addDisposable(getUserLeaderboards.get(typeEntity, rangeEntity, timespanEntity, companyId, page, pageSize)
                .map { it.map { userRankingMapper.mapFrom(it) } }
                .doOnSubscribe{
                    loadingState.value = true
                }
                .subscribe({

                    loadingState.value = false
                    newLeaderboardPage.value = it

                }, {

                    loadingState.value = false
                    errorState.value = "Impossibile ottenere la classifica al momento"

                }))

    }
}