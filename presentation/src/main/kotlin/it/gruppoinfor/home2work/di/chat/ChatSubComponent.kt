package it.gruppoinfor.home2work.di.chat

import dagger.Subcomponent
import it.gruppoinfor.home2work.chat.ChatActivity


@ChatScope
@Subcomponent(modules = [ChatModule::class])
interface ChatSubComponent {
    fun inject(chatActivity: ChatActivity)
}