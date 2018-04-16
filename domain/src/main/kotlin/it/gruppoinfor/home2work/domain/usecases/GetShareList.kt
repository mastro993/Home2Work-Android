package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository


class GetShareList(
        transformer: Transformer<List<ShareEntity>>,
        private val shareRepository: ShareRepository
) : UseCase<List<ShareEntity>>(transformer) {

    companion object {
        private const val PARAM_LIMIT = "param:limit"
        private const val PARAM_PAGE = "param:page"
    }

    fun get(page: Int?, limit: Int?): Observable<List<ShareEntity>> {
        val data = HashMap<String, Any>()

        page?.let { data[PARAM_PAGE] = it }
        limit?.let { data[PARAM_LIMIT] = it }

        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<List<ShareEntity>> {

        val page = data?.get(PARAM_PAGE) as Int?
        val limit = data?.get(PARAM_LIMIT) as Int?

        return shareRepository.getShareList(limit, page)
    }
}