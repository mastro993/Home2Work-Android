package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.MatchEntity
import it.gruppoinfor.home2work.domain.interfaces.MatchRepository


class GetMatchList(
        transformer: Transformer<List<MatchEntity>>,
        private val matchRepository: MatchRepository
) : UseCase<List<MatchEntity>>(transformer) {

    override fun createObservable(data: Map<String, Any>?): Observable<List<MatchEntity>> {
        return matchRepository.getMatchList()
    }
}