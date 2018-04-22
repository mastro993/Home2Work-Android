package it.gruppoinfor.home2work.domain.interfaces

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.ProfileEntity
import it.gruppoinfor.home2work.domain.entities.UserEntity
import java.io.File


interface UserRepository {
    fun login(email: String, password: String): Observable<UserEntity>
    fun setAvatar(avatar: File): Observable<Boolean>
    fun getProfile(): Observable<ProfileEntity>
    fun getUser(): Observable<UserEntity>
    fun getUser(userId: Long): Observable<UserEntity>
    fun getUserList(): Observable<List<UserEntity>>
    fun getUserProfile(userId: Long): Observable<ProfileEntity>
    fun updateStatus(status:String): Observable<Boolean>
    fun hideStatus(): Observable<Boolean>
}