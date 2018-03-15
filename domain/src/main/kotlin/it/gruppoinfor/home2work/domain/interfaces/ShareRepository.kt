package it.gruppoinfor.home2work.domain.interfaces

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.LatLng
import it.gruppoinfor.home2work.domain.entities.Share

interface ShareRepository {
    fun getCompletedShare(shareId: Long): Observable<Share>
    fun getCompletedShareList(): Observable<List<Share>>
    fun getActiveShare(): Observable<Share>
    fun createShare(): Observable<Share>
    fun joinShare(shareId: Long, joinLatLng: LatLng): Observable<Share>
    fun completeShare(completeLocation: LatLng): Observable<Boolean>
    fun finishShare(): Observable<Boolean>
    fun banUserFromShare(userId: Long): Observable<Boolean>
    fun cancelShare(): Observable<Boolean>
    fun leaveShare(): Observable<Boolean>
}