package it.gruppoinfor.home2work.domain.interfaces

import it.gruppoinfor.home2work.domain.entities.UserEntity

interface PreferencesRepository {
    var sessionToken: String?
    var lastEmail: String?
    var firebaseToken: String?
    var user: UserEntity?
    fun clear()
}