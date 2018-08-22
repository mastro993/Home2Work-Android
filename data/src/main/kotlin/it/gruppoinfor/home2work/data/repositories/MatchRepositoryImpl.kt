package it.gruppoinfor.home2work.data.repositories

import io.reactivex.Observable
import it.gruppoinfor.home2work.data.api.APIService
import it.gruppoinfor.home2work.data.api.get
import it.gruppoinfor.home2work.data.api.services.MatchService
import it.gruppoinfor.home2work.data.mappers.MatchDataEntityMapper
import it.gruppoinfor.home2work.data.mappers.MatchEntityDataMapper
import it.gruppoinfor.home2work.domain.entities.MatchEntity
import it.gruppoinfor.home2work.domain.entities.Optional
import it.gruppoinfor.home2work.domain.interfaces.MatchRepository


class MatchRepositoryImpl(
        private val matchDataEntityMapper: MatchDataEntityMapper,
        private val matchEntityDataMapper: MatchEntityDataMapper
) : MatchRepository {

    private val matchService = APIService.get<MatchService>()

    override fun getMatchList(limit: Int?, page: Int?): Observable<List<MatchEntity>> {
        return matchService.getMatchList(limit, page).map {matches ->
            matches.map { matchDataEntityMapper.mapFrom(it) }
        }
    }

    override fun getMatch(matchId: Long): Observable<Optional<MatchEntity>> {
        return matchService.getMatchById(matchId).map {
            matchDataEntityMapper.mapOptional(it)
        }
    }

    override fun editMatch(match: MatchEntity): Observable<Boolean> {
        val matchData = matchEntityDataMapper.mapFrom(match)
        return matchService.editMatch(matchData)
    }
}