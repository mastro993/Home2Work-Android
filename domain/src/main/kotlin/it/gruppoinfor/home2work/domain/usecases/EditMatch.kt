package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.LatLngEntity
import it.gruppoinfor.home2work.domain.entities.MatchEntity
import it.gruppoinfor.home2work.domain.interfaces.MatchRepository

class EditMatch(
        transformer: Transformer<Boolean>,
        private val matchRepository: MatchRepository
) : UseCase<Boolean>(transformer) {

    companion object {
        private const val PARAM_MATCH = "param:match"
    }

    fun edit(matchEntity: MatchEntity): Observable<Boolean>{
        val data = HashMap<String, MatchEntity>()
        data[PARAM_MATCH] = matchEntity
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Boolean> {
        val match = data?.get(PARAM_MATCH)
        match?.let {
            return matchRepository.editMatch(match as MatchEntity)
        } ?: return Observable.error(IllegalArgumentException("match must be provided."))
    }
}