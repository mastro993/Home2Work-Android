package it.gruppoinfor.home2work.domain.interfaces


import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.Match

interface MatchRepository {
    fun getUserMatchList(userId: Long): Observable<List<Match>>
    fun getMatch(matchId: Long): Observable<Match>
    fun editMatch(match: Match): Observable<Boolean>
}