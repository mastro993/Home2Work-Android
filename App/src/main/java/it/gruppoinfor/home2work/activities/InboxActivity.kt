package it.gruppoinfor.home2work.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.dialogs.DialogsListAdapter
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.fragments.HomeFragment
import it.gruppoinfor.home2work.utils.Const
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.inbox.Chat
import kotlinx.android.synthetic.main.activity_inbox.*

class InboxActivity : AppCompatActivity() {


    private var dialogsListAdapter: DialogsListAdapter<Chat>? = null

    private val mMessageReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            refreshList()

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbox)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        initUI()
    }

    override fun onStart() {
        super.onStart()

        // TODO aggiornamento lista per nuovo messaggio
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,
                IntentFilter(Const.NEW_MESSAGE_RECEIVED)
        )

        inbox_loading_view.visibility = View.VISIBLE
        refreshList()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

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

        inbox_loading_view.visibility = View.VISIBLE

        dialogsListAdapter = DialogsListAdapter(ImageLoader { imageView, url ->
            val requestOptions = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .circleCrop()
                    .placeholder(R.drawable.ic_avatar_placeholder)

            Glide.with(this@InboxActivity)
                    .load(url)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(requestOptions)
                    .into(imageView)
        })

        dialogs_list.setAdapter(dialogsListAdapter)

        if (HomeToWorkClient.chatList == null) {
            refreshList()
        } else {
            dialogsListAdapter?.setItems(HomeToWorkClient.chatList)
            inbox_loading_view.visibility = View.GONE
        }

        dialogsListAdapter?.setOnDialogClickListener({
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(Const.EXTRA_CHAT, it)
            startActivityForResult(intent, 0)
        })

        dialogsListAdapter?.setOnDialogLongClickListener({
            // TODO long click
        })

        swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh_layout.setOnRefreshListener {
            swipe_refresh_layout.isRefreshing = true
            refreshList()
        }

    }

    private fun refreshList() {



        HomeToWorkClient.getChatList(OnSuccessListener {
            chatList = it
            dialogsListAdapter?.setItems(it)
            swipe_refresh_layout.isRefreshing = false
            inbox_loading_view.visibility = View.GONE
            HomeFragment.inboxIcon.setCount(it.count { it.unreadCnt > 0 })
        }, OnFailureListener {
            // TODO avviso errore
            swipe_refresh_layout.isRefreshing = false
            inbox_loading_view.visibility = View.GONE
        })

    }

    companion object {
        var chatList: List<Chat>? = null
    }


}
