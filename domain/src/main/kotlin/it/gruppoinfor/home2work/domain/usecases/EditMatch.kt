package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.Match
import it.gruppoinfor.home2work.domain.interfaces.MatchRepository

class EditMatch(
        transformer: Transformer<Boolean>,
        private val matchRepository: MatchRepository
) : UseCase<Boolean>(transformer) {

    companion object {
        private const val PARAM_MATCH = "param:match"
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Boolean> {
        val match = data?.get(PARAM_MATCH)
        match?.let {
            return matchRepository.editMatch(match as Match)
        } ?: return Observable.error(IllegalArgumentException("match must be provided."))
    }
}