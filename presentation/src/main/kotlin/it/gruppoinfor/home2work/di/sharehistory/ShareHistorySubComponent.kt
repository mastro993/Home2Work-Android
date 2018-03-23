package it.gruppoinfor.home2work.di.sharehistory

import dagger.Subcomponent
import it.gruppoinfor.home2work.sharehistory.ShareHistoryActivity


@ShareHistoryScope
@Subcomponent(modules = [ShareHistoryModule::class])
interface ShareHistorySubComponent {
    fun inject(sharesHistoryActivity: ShareHistoryActivity)
}