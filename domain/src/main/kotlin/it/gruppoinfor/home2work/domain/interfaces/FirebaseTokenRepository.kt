package it.gruppoinfor.home2work.domain.interfaces

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.Location

interface FirebaseTokenRepository {
    fun syncUserFCMToken(token: String): Observable<Boolean>
}