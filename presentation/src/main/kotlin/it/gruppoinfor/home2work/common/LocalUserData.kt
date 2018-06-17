package it.gruppoinfor.home2work.common

import it.gruppoinfor.home2work.common.mappers.UserEntityUserMapper
import it.gruppoinfor.home2work.common.mappers.UserUserEntityMapper
import it.gruppoinfor.home2work.domain.interfaces.PreferencesRepository
import it.gruppoinfor.home2work.entities.Share
import it.gruppoinfor.home2work.entities.User


class LocalUserData constructor(
        private val preferencesRepository: PreferencesRepository,
        private val userEntityUserMapper: UserEntityUserMapper,
        private val userEntityMapper: UserUserEntityMapper
) {

    private var mUser: User? = null
    var currentShare: Share? = null

    var user: User?
        get() {
            mUser?.let {
                return it
            } ?: let {
                preferencesRepository.user?.let {
                    return userEntityUserMapper.mapFrom(it)
                } ?: return null
            }
        }
        set(value) {
            value?.let {
                mUser = it
                preferencesRepository.user = userEntityMapper.mapFrom(it)
            }
        }

    var session: String?
        get() {
            return preferencesRepository.sessionToken
        }
        set(value) {
            preferencesRepository.sessionToken = value
        }

    var firebaseToken: String?
        get() {
            return preferencesRepository.firebaseToken
        }
        set(value) {
            preferencesRepository.firebaseToken = value
        }

    var email: String?
        get() {
            return preferencesRepository.lastEmail
        }
        set(value) {
            preferencesRepository.lastEmail = value
        }

    fun clear() {
        session = null
        preferencesRepository.clear()
    }

}