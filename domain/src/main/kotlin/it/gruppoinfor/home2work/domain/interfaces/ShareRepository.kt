package it.gruppoinfor.home2work.domain.interfaces

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.GuestEntity
import it.gruppoinfor.home2work.domain.entities.Optional
import it.gruppoinfor.home2work.domain.entities.ShareEntity

interface ShareRepository {
    fun getShare(shareId: Long): Observable<Optional<ShareEntity>>
    fun getShareList(limit: Int?, page: Int?): Observable<List<ShareEntity>>
    fun getActiveShare(): Observable<ShareEntity>
    fun getShareGuests(shareId: Long): Observable<List<GuestEntity>>
    fun createShare(startLat: Double, startLng: Double): Observable<ShareEntity>
    fun joinShare(shareId: Long, joinLat: Double, joinLng: Double): Observable<ShareEntity>
    fun completeShare(completeLat: Double, completeLng: Double): Observable<ShareEntity>
    fun finishShare(finishLat: Double, finishLng: Double): Observable<ShareEntity>
    fun banUserFromShare(userId: Long): Observable<Boolean>
    fun cancelShare(): Observable<Boolean>
    fun leaveShare(): Observable<Boolean>
}