package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.LatLngEntity
import it.gruppoinfor.home2work.domain.entities.MatchEntity
import it.gruppoinfor.home2work.domain.entities.Optional
import it.gruppoinfor.home2work.domain.interfaces.MatchRepository
import java.util.regex.Matcher

class GetMatch(
        transformer: Transformer<Optional<MatchEntity>>,
        private val matchRepository: MatchRepository
) : UseCase<Optional<MatchEntity>>(transformer) {

    companion object {
        private const val PARAM_MATCH_ID = "param:matchId"
    }

    fun getById(matchId: Long): Observable<Optional<MatchEntity>>{
        val data = HashMap<String, Long>()
        data[PARAM_MATCH_ID] = matchId
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Optional<MatchEntity>> {
        val matchId = data?.get(PARAM_MATCH_ID)
        matchId?.let {
            return matchRepository.getMatch(matchId as Long)
        } ?: return Observable.error(IllegalArgumentException("matchId must be provided."))
    }
}