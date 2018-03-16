package it.gruppoinfor.home2work.domain.interfaces

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.Optional
import it.gruppoinfor.home2work.domain.entities.UserEntity
import it.gruppoinfor.home2work.domain.entities.UserProfileEntity


interface UserRepository {
    fun login(email: String, password: String): Observable<Optional<UserEntity>>
    fun login(token: String): Observable<Optional<UserEntity>>
    fun getProfile(): Observable<UserProfileEntity>
    fun getUser(userId: Long): Observable<Optional<UserEntity>>
    fun getUserList(): Observable<List<UserEntity>>
    fun getUserProfile(userId: Long): Observable<UserProfileEntity>
}