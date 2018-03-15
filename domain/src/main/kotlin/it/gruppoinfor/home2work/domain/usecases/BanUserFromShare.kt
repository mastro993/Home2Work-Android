package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository

class BanUserFromShare(
        transformer: Transformer<Boolean>,
        private val shareRepository: ShareRepository
) : UseCase<Boolean>(transformer) {

    companion object {
        private const val PARAM_USER_ID = "param:userId"
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Boolean> {
        val userId = data?.get(PARAM_USER_ID)

        userId?.let {
            return shareRepository.banUserFromShare(userId as Long)
        } ?: return Observable.error(IllegalArgumentException("userId must be provided."))
    }
}