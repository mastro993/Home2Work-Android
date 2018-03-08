package it.gruppoinfor.home2work.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.view.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.chat.ChatListActivity
import it.gruppoinfor.home2work.chat.ChatIcon
import it.gruppoinfor.home2work.firebase.MessagingService
import it.gruppoinfor.home2workapi.HomeToWorkClient


class HomeFragment : Fragment() {

    private val mMessageReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            refreshInbox()

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onStart() {
        super.onStart()

        LocalBroadcastManager.getInstance(context!!).registerReceiver(
                mMessageReceiver,
                IntentFilter(MessagingService.NEW_MESSAGE_RECEIVED)
        )

        refreshInbox()
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(mMessageReceiver)
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_home, menu)

        val item = menu.findItem(R.id.action_messages)
        chatIcon = item.actionView as ChatIcon

    }

    private fun refreshInbox() {
        chatIcon?.setCount(ChatListActivity.chatList.count { it.unreadCnt > 0 })
        HomeToWorkClient.getChatList(OnSuccessListener { list ->
            ChatListActivity.chatList = ArrayList(list)
            chatIcon?.setCount(list.count { it.unreadCnt > 0 })
        }, OnFailureListener {

        })
    }

    companion object {
        var chatIcon: ChatIcon? = null
    }


}
