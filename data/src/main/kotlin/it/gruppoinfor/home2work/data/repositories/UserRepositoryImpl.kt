package it.gruppoinfor.home2work.data.repositories

import io.reactivex.Observable
import it.gruppoinfor.home2work.data.api.APIServiceGenerator
import it.gruppoinfor.home2work.data.api.getService
import it.gruppoinfor.home2work.data.api.services.UserService
import it.gruppoinfor.home2work.data.mappers.ProfileDataEntityMapper
import it.gruppoinfor.home2work.data.mappers.UserDataEntityMapper
import it.gruppoinfor.home2work.domain.entities.ProfileEntity
import it.gruppoinfor.home2work.domain.entities.UserEntity
import it.gruppoinfor.home2work.domain.interfaces.UserRepository
import java.io.File


class UserRepositoryImpl(
        private val userMapper: UserDataEntityMapper,
        private val profileMapper: ProfileDataEntityMapper
) : UserRepository {

    private val userService: UserService = APIServiceGenerator.getService()

    override fun login(email: String, password: String): Observable<UserEntity> {
        return userService.login(email, password).map {
            userMapper.mapFrom(it)
        }
    }

    override fun login(token: String): Observable<UserEntity> {
        return userService.login(token).map {
            userMapper.mapFrom(it)
        }
    }

    override fun setAvatar(avatar: File): Observable<Boolean> {
        // TODO Caricamento avatar
        return Observable.just(false)
    }

    override fun getProfile(): Observable<ProfileEntity> {
        return userService.getProfile().map {
            profileMapper.mapFrom(it)
        }
    }

    override fun getUser(): Observable<UserEntity> {
        return userService.get().map {
            userMapper.mapFrom(it)
        }
    }

    override fun getUser(userId: Long): Observable<UserEntity> {
        return userService.getUserById(userId).map {
            userMapper.mapFrom(it)
        }
    }

    override fun getUserList(): Observable<List<UserEntity>> {
        // Non implementato
        return Observable.just(null)
    }

    override fun getUserProfile(userId: Long): Observable<ProfileEntity> {
        return userService.getUserProfileById(userId).map {
            profileMapper.mapFrom(it)
        }
    }
}