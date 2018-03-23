package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.Optional
import it.gruppoinfor.home2work.domain.entities.UserEntity
import it.gruppoinfor.home2work.domain.interfaces.UserRepository


class UserLogin(
        transformer: Transformer<UserEntity>,
        private val userRepository: UserRepository
) : UseCase<UserEntity>(transformer) {

    companion object {
        private const val PARAM_EMAIL = "param:email"
        private const val PARAM_PASSWORD = "param:password"
    }

    fun login(email: String, password: String): Observable<UserEntity> {
        val data = HashMap<String, String>()
        data[PARAM_EMAIL] = email
        data[PARAM_PASSWORD] = password
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<UserEntity> {
        val email = data?.get(PARAM_EMAIL)
        val password = data?.get(PARAM_PASSWORD)

        email?.let {
            password?.let {
                return userRepository.login(email as String, password as String)
            } ?: return Observable.error(IllegalArgumentException("password must be provided."))
        } ?: return Observable.error(IllegalArgumentException("email must be provided."))


    }

}