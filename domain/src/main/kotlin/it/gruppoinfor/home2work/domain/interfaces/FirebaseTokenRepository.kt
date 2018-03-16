package it.gruppoinfor.home2work.domain.interfaces

import io.reactivex.Observable

interface FirebaseTokenRepository {
    fun syncUserFCMToken(token: String): Observable<Boolean>
}