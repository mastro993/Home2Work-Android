package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.interfaces.UserRepository


class UpdateStatus(
        transformer: Transformer<Boolean>,
        private val userRepository: UserRepository
) : UseCase<Boolean>(transformer) {

    companion object {
        private const val PARAM_STATUS = "param:status"
    }

    fun update(status: String): Observable<Boolean> {
        val data = HashMap<String, String>()
        data[PARAM_STATUS] = status
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Boolean> {
        val status = data?.get(PARAM_STATUS)

        status?.let {
            return userRepository.updateStatus(status as String)
        } ?: return Observable.error(IllegalArgumentException("status must be provided."))


    }

}