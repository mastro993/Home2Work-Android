package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.MatchEntity
import it.gruppoinfor.home2work.domain.interfaces.MatchRepository


class GetMatchList(
        transformer: Transformer<List<MatchEntity>>,
        private val matchRepository: MatchRepository
) : UseCase<List<MatchEntity>>(transformer) {

    companion object {
        private const val PARAM_LIMIT = "param:limit"
        private const val PARAM_PAGE = "param:page"
    }

    fun get(page: Int?, limit: Int?): Observable<List<MatchEntity>> {
        val data = HashMap<String, Any>()

        page?.let { data[PARAM_PAGE] = it }
        limit?.let { data[PARAM_LIMIT] = it }

        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<List<MatchEntity>> {

        val page = data?.get(PARAM_PAGE) as Int?
        val limit = data?.get(PARAM_LIMIT) as Int?

        return matchRepository.getMatchList(limit, page)
    }
}