package it.gruppoinfor.home2work.domain.interfaces


import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.MatchEntity
import it.gruppoinfor.home2work.domain.entities.Optional

interface MatchRepository {
    fun getMatchList(limit: Int?, page: Int?): Observable<List<MatchEntity>>
    fun getMatch(matchId: Long): Observable<Optional<MatchEntity>>
    fun editMatch(match: MatchEntity): Observable<Boolean>
}