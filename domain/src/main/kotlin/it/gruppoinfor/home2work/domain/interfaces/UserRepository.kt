package it.gruppoinfor.home2work.domain.interfaces

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.ClientUser
import it.gruppoinfor.home2work.domain.entities.User


interface UserRepository {
    fun login(email: String, password: String): Observable<ClientUser>
    fun login(token: String): Observable<ClientUser>
    fun getUserDetails(userId: Long): Observable<User>
    fun getUserList(): Observable<List<User>>
}