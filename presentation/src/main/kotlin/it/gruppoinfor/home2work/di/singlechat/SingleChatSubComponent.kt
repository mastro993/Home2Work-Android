package it.gruppoinfor.home2work.di.singlechat

import dagger.Subcomponent
import it.gruppoinfor.home2work.chat.SingleChatActivity


@SingleChatScope
@Subcomponent(modules = [SingleChatModule::class])
interface SingleChatSubComponent {
    fun inject(singleChatActivity: SingleChatActivity)
}