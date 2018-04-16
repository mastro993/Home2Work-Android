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

    override fun getShareList(limit: Int?, page: Int?): Observable<List<ShareEntity>> {
        return shareService.getCompletedShareList(limit, page).map {
            it.map { mapper.mapFrom(it) }
        }
    }

    override fun getActiveShare(): Observable<ShareEntity> {
        return shareService.getCurrentShare().map {
            mapper.mapFrom(it)
        }
    }

    override fun createShare(startLat: Double, startLng: Double): Observable<ShareEntity> {
        return shareService.createShare(startLat, startLng).map {
            mapper.mapFrom(it)
        }
    }

    override fun joinShare(shareId: Long, joinLat: Double, joinLng: Double): Observable<ShareEntity> {
        return shareService.joinShare(shareId, joinLat, joinLng).map {
            mapper.mapFrom(it)
        }
    }

    override fun completeShare(completeLat: Double, completeLng: Double): Observable<ShareEntity> {
        return shareService.completeCurrentShare(completeLat, completeLng)
                .map { mapper.mapFrom(it) }
    }

    override fun finishShare(finishLat: Double, finishLng: Double): Observable<ShareEntity> {
        return shareService.finishCurrentShare(finishLat, finishLng)
                .map { mapper.mapFrom(it) }
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