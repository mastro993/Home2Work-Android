package it.gruppoinfor.home2work.data.repositories

import io.reactivex.Observable
import it.gruppoinfor.home2work.data.api.APIServiceGenerator
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

    private val matchService = APIServiceGenerator.createService(MatchService::class.java)

    override fun getMatchList(): Observable<List<MatchEntity>> {
        return matchService.getMatchList().map {
            it.map { matchDataEntityMapper.mapFrom(it) }
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