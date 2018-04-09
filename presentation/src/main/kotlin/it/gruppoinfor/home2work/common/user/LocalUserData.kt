package it.gruppoinfor.home2work.common.user

import it.gruppoinfor.home2work.entities.Share
import it.gruppoinfor.home2work.entities.User


class LocalUserData constructor(
        private val userPreferences: UserPreferences,
        private val settingsPreferences: SettingsPreferences
) {

    private var mUser: User? = null
    var currentShare: Share? = null

    var user: User?
        get() {
            mUser?.let { return it } ?: return userPreferences.getUserData()
        }
        set(value) {
            value?.let {
                mUser = it
                userPreferences.setUserData(it)
            }
        }

    var session: String?
        get() {
            return userPreferences.getSessionToken()
        }
        set(value) = userPreferences.setSessionToken(value)

    var firebaseToken: String?
        get() {
            return userPreferences.getFirebaseToken()
        }
        set(value) {
            userPreferences.setFirebaseToken(value)
        }

    var email: String?
        get() {
            return userPreferences.getEmail()
        }
        set(value) = userPreferences.setEmail(value)

    fun clear() {
        userPreferences.clearUserData()
        userPreferences.clearSessionToken()
    }

}