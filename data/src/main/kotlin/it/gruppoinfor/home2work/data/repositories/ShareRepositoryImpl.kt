package it.gruppoinfor.home2work.data.repositories

import io.reactivex.Observable
import it.gruppoinfor.home2work.data.api.APIService
import it.gruppoinfor.home2work.data.api.get
import it.gruppoinfor.home2work.data.api.services.ShareService
import it.gruppoinfor.home2work.data.mappers.GuestDataEntityMapper
import it.gruppoinfor.home2work.data.mappers.ShareDataEntityMapper
import it.gruppoinfor.home2work.domain.entities.GuestEntity
import it.gruppoinfor.home2work.domain.entities.Optional
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository


class ShareRepositoryImpl(
        private val mapper: ShareDataEntityMapper,
        private val guestMapper: GuestDataEntityMapper
) : ShareRepository {

    private val shareService = APIService.get<ShareService>()

    override fun getShare(shareId: Long): Observable<Optional<ShareEntity>> {
        return shareService.getCompletedShare(shareId).map {
            mapper.mapOptional(it)
        }
    }

    override fun getShareGuests(shareId: Long): Observable<List<GuestEntity>> {
        return shareService.getShareGuests(shareId).map {
            it.map { guestMapper.mapFrom(it) }
        }
    }

    override fun getShareList(): Observable<List<ShareEntity>> {
        return shareService.getCompletedShareList().map {
            it.map { mapper.mapFrom(it) }
        }
    }

    override fun getActiveShare(): Observable<ShareEntity> {
        return shareService.getCurrentShare().map {
            mapper.mapFrom(it)
        }
    }

    override fun createShare(): Observable<ShareEntity> {
        return shareService.createShare().map {
            mapper.mapFrom(it)
        }
    }

    override fun joinShare(shareId: Long, joinLat: Double, joinLng: Double): Observable<ShareEntity> {
        return shareService.joinShare(shareId, joinLat, joinLng).map {
            mapper.mapFrom(it)
        }
    }

    override fun completeShare(completeLat: Double, completeLng: Double): Observable<Boolean> {
        return shareService.completeCurrentShare(completeLat, completeLng)
    }

    override fun finishShare(): Observable<Boolean> {
        return shareService.finishCurrentShare()
    }

    override fun banUserFromShare(userId: Long): Observable<Boolean> {
        return shareService.banGuestFromCurrentShare(userId)
    }

    override fun cancelShare(): Observable<Boolean> {
        return shareService.cancelCurrentShare()
    }

    override fun leaveShare(): Observable<Boolean> {
        return shareService.leaveCurrentShare()
    }
}