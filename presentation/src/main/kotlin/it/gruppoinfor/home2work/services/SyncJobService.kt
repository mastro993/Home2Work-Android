package it.gruppoinfor.home2work.services

import android.app.job.JobParameters
import android.app.job.JobService
import io.reactivex.Observable
import it.gruppoinfor.home2work.di.DipendencyInjector
import it.gruppoinfor.home2work.domain.usecases.DeleteUserLocations
import it.gruppoinfor.home2work.domain.usecases.GetUserLocations
import it.gruppoinfor.home2work.domain.usecases.SyncUserLocations
import timber.log.Timber
import javax.inject.Inject


class SyncJobService : JobService() {

    @Inject
    lateinit var getUserLocations: GetUserLocations
    @Inject
    lateinit var syncUserLocation: SyncUserLocations
    @Inject
    lateinit var deleteUserLocations: DeleteUserLocations

    override fun onCreate() {
        super.onCreate()
        DipendencyInjector.mainComponent.inject(this)
    }

    override fun onStartJob(params: JobParameters): Boolean {

        Timber.i("Inizio job di sincronizzazione")

        val userId = params.extras.getLong(KEY_USER_ID)

        getUserLocations.byId(userId)
                .flatMap {
                    if (it.isNotEmpty()) {
                        Timber.v("Sincronizzazione di ${it.size} posizioni utente")
                        syncUserLocation.upload(it)
                    } else {
                        Timber.v("Nessuna posizione da sincronizzare")
                        Observable.just(false)
                    }
                }
                .subscribe({
                    if (it) {
                        Timber.i("Sincronizzazione completata")
                        deleteUserLocations.byId(userId).subscribe({
                            Timber.i("Posizioni eliminate")
                        })
                    }
                    jobFinished(params, true)
                }, {
                    Timber.e(it, "Sincronizzazione fallita")
                    jobFinished(params, true)
                })

        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        Timber.i("Fine job di sincronizzazione")
        return false
    }

    companion object {
        const val ID: Int = 2342
        const val KEY_USER_ID: String = "user_id"
    }
}
