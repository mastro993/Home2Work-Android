package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.Match
import it.gruppoinfor.home2work.domain.interfaces.MatchRepository


class GetUserMatchList(
        transformer: Transformer<List<Match>>,
        private val matchRepository: MatchRepository
) : UseCase<List<Match>>(transformer) {

    companion object {
        private const val PARAM_USER_ID = "param:userId"
    }

    override fun createObservable(data: Map<String, Any>?): Observable<List<Match>> {
        val userId = data?.get(PARAM_USER_ID)

        userId?.let {
            return matchRepository.getUserMatchList(userId as Long)
        } ?: return Observable.error(IllegalArgumentException("userId must be provided."))

    }
}