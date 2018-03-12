package it.gruppoinfor.home2work.chat

import android.content.Context
import android.content.Intent
import it.gruppoinfor.home2work.common.ActivityArgs
import org.jetbrains.anko.intentFor


class ChatActivityArgs(
        val chatId: Long,
        val recipientId: Long,
        val recipientName: String
) : ActivityArgs {

    override fun intent(activity: Context): Intent = activity.intentFor<ChatActivity>()
            .apply {
                putExtra(KEY_CHAT_ID, chatId)
                putExtra(KEY_RECIPIENT_ID, recipientId)
                putExtra(KEY_RECIPIENT_NAME, recipientName)
            }

    companion object {

        private const val KEY_CHAT_ID: String = "chat_id"
        private const val KEY_RECIPIENT_ID: String = "recipient_id"
        private const val KEY_RECIPIENT_NAME: String = "recipient_name"

        fun deserializeFrom(intent: Intent): ChatActivityArgs {
            return ChatActivityArgs(
                    chatId = intent.getLongExtra(KEY_CHAT_ID, 0L),
                    recipientId = intent.getLongExtra(KEY_RECIPIENT_ID, 0L),
                    recipientName = intent.getStringExtra(KEY_RECIPIENT_NAME)
            )
        }
    }

}

