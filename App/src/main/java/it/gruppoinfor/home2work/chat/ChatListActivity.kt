package it.gruppoinfor.home2work.chat

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.dialogs.DialogsListAdapter
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.home.HomeFragment
import it.gruppoinfor.home2work.firebase.MessagingService
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.chat.Chat
import kotlinx.android.synthetic.main.activity_inbox.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop


class ChatListActivity : AppCompatActivity() {


    private var dialogsListAdapter: DialogsListAdapter<Chat>? = null

    private val mMessageReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            refreshChatList()

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       // val timings = TimingLogger("TIMING_LOGGER", "methodA")


        setContentView(R.layout.activity_inbox)
        //timings.addSplit("SET CONTENT VIEW")

        initUI()
        //timings.addSplit("initUI")
        getChatList()
        //timings.addSplit("getchattList")

        //timings.dumpToLog()

    }

    override fun onStart() {
        super.onStart()

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,
                IntentFilter(MessagingService.NEW_MESSAGE_RECEIVED)
        )

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // TODO menu

        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        dialogsListAdapter?.notifyDataSetChanged()

    }

    private fun initUI() {

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh_layout.setOnRefreshListener {
            swipe_refresh_layout.isRefreshing = true
            refreshChatList()
        }

        // TODO custom adapter
        // https://github.com/stfalcon-studio/ChatKit/blob/master/docs/COMPONENT_DIALOGS_LIST.MD
        dialogsListAdapter = DialogsListAdapter(ImageLoader { imageView, url ->
            val requestOptions = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .circleCrop()
                    .placeholder(R.drawable.ic_avatar_placeholder)

            Glide.with(this@ChatListActivity)
                    .load(url)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(requestOptions)
                    .into(imageView)
        })

        dialogsListAdapter?.setOnDialogClickListener({
            startActivity(intentFor<ChatActivity>(ChatActivity.EXTRA_CHAT to it).singleTop())
        })
        dialogsListAdapter?.setOnDialogLongClickListener({
            // TODO long click
        })

        dialogs_list.setAdapter(dialogsListAdapter)

        if (chatList.isNotEmpty()) {
            status_view.done()
            dialogsListAdapter?.setItems(chatList)
            refreshChatList()
        } else {
            status_view.loading()
            getChatList()
        }


    }

    private fun getChatList() {

        HomeToWorkClient.getChatList(OnSuccessListener { list ->

            chatList.clear()
            chatList.addAll(list)
            dialogsListAdapter?.setItems(list)

            refreshUI()

        }, OnFailureListener {

            status_view.error("Impossibile caricare lista conversazioni al momento")

        })

    }

    private fun refreshChatList() {

        HomeToWorkClient.getChatList(OnSuccessListener { list ->

            chatList.clear()
            chatList.addAll(list)
            dialogsListAdapter?.setItems(list)

            refreshUI()

        }, OnFailureListener {

            swipe_refresh_layout.isRefreshing = false
            Toast.makeText(this, "Impossibile aggiornare lista conversazioni al momento", Toast.LENGTH_SHORT).show()
            it.printStackTrace()

        })

    }

    private fun refreshUI() {

        if (chatList.isNotEmpty()) {

            HomeFragment.chatIcon?.setCount(chatList.count { it.unreadCnt > 0 })
            removeNotification()

            status_view.done()

        } else {

            status_view.empty("Non hai ancora nessuna conversazione con altri utenti")

        }

        swipe_refresh_layout.isRefreshing = false

    }

    private fun removeNotification() {

        val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotifyMgr.cancel(MessagingService.MESSAGING_NOTIFICATION_ID)

    }

    companion object {
        var chatList: ArrayList<Chat> = ArrayList()
    }


}
