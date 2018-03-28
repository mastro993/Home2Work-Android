package it.gruppoinfor.home2work.data.repositories

import io.reactivex.Observable
import it.gruppoinfor.home2work.data.api.APIServiceGenerator
import it.gruppoinfor.home2work.data.api.getService
import it.gruppoinfor.home2work.data.api.services.ShareService
import it.gruppoinfor.home2work.data.mappers.ShareDataEntityMapper
import it.gruppoinfor.home2work.domain.entities.Optional
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import it.gruppoinfor.home2work.domain.interfaces.ShareRepository


class ShareRepositoryImpl(
        private val mapper: ShareDataEntityMapper
) : ShareRepository {

    private val shareService: ShareService = APIServiceGenerator.getService()

    override fun getShare(shareId: Long): Observable<Optional<ShareEntity>> {
        return shareService.getShare(shareId).map {
            mapper.mapOptional(it)
        }
    }

    override fun getShareList(): Observable<List<ShareEntity>> {
        return shareService.getShareList().map {
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