package it.gruppoinfor.home2work.data.repositories

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.Optional
import it.gruppoinfor.home2work.domain.entities.UserEntity
import it.gruppoinfor.home2work.domain.entities.UserProfileEntity
import it.gruppoinfor.home2work.domain.interfaces.UserRepository


class UserRepositoryImpl : UserRepository {
    override fun login(email: String, password: String): Observable<Optional<UserEntity>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun login(token: String): Observable<Optional<UserEntity>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getProfile(): Observable<UserProfileEntity> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUser(userId: Long): Observable<Optional<UserEntity>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUserList(): Observable<List<UserEntity>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUserProfile(userId: Long): Observable<UserProfileEntity> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}