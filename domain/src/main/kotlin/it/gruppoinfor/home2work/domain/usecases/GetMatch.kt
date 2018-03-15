package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.Match
import it.gruppoinfor.home2work.domain.interfaces.MatchRepository

class GetMatch(
        transformer: Transformer<Match>,
        private val matchRepository: MatchRepository
) : UseCase<Match>(transformer) {

    companion object {
        private const val PARAM_MATCH_ID = "param:matchId"
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Match> {
        val matchId = data?.get(PARAM_MATCH_ID)
        matchId?.let {
            return matchRepository.getMatch(matchId as Long)
        } ?: return Observable.error(IllegalArgumentException("matchId must be provided."))
    }
}