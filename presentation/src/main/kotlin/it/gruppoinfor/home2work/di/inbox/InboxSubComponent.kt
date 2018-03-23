package it.gruppoinfor.home2work.di.inbox

import dagger.Subcomponent
import it.gruppoinfor.home2work.inbox.InboxActivity


@InboxScope
@Subcomponent(modules = [InboxModule::class])
interface InboxSubComponent {
    fun inject(inboxActivity: InboxActivity)
}