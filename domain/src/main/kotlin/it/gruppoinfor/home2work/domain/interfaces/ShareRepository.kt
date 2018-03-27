package it.gruppoinfor.home2work.domain.interfaces

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.Optional
import it.gruppoinfor.home2work.domain.entities.ShareEntity

interface ShareRepository {
    fun getShare(shareId: Long): Observable<Optional<ShareEntity>>
    fun getShareList(): Observable<List<ShareEntity>>
    fun getActiveShare(): Observable<ShareEntity>
    fun createShare(): Observable<ShareEntity>
    fun joinShare(shareId: Long, joinLat: Double, joinLng: Double): Observable<ShareEntity>
    fun completeShare(completeLat: Double, completeLng: Double): Observable<Boolean>
    fun finishShare(): Observable<Boolean>
    fun banUserFromShare(userId: Long): Observable<Boolean>
    fun cancelShare(): Observable<Boolean>
    fun leaveShare(): Observable<Boolean>
}