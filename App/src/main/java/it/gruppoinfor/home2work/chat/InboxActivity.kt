package it.gruppoinfor.home2work.chat

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.dialogs.DialogsListAdapter
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.extensions.showToast
import it.gruppoinfor.home2work.firebase.MessagingService
import it.gruppoinfor.home2work.firebase.NewMessageEvent
import kotlinx.android.synthetic.main.activity_inbox.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class InboxActivity : AppCompatActivity(), InboxView {

    private var mInboxPresenter: InboxPresenter = InboxPresenterImpl(this)

    private var dialogsListAdapter: DialogsListAdapter<Chat>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbox)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initUI()

        mInboxPresenter.onCreate()

    }

    override fun onResume() {
        super.onResume()

        EventBus.getDefault().register(this)

        mInboxPresenter.onResume()

        // Rimuovo eventuali notitifiche per nuovi messaggi ricevuti
        val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotifyMgr.cancel(MessagingService.MESSAGING_NOTIFICATION_ID)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        mInboxPresenter.onMessage()

    }

    override fun setItems(list: List<Chat>) {

        dialogsListAdapter?.setItems(list)
        dialogsListAdapter?.notifyDataSetChanged()

        status_view.done()

    }

    override fun onRefresh() {

        swipe_refresh_layout.isRefreshing = true

    }

    override fun showErrorMessage(errorMessage: String) {

        swipe_refresh_layout.isRefreshing = false
        showToast(errorMessage)

    }

    override fun onLoading() {

        status_view.loading()

    }

    override fun onLoadingError(errorMessage: String) {

        status_view.error(errorMessage)

    }

    override fun onRefreshComplete() {

        swipe_refresh_layout.isRefreshing = false

    }

    override fun onPause() {
        super.onPause()

        EventBus.getDefault().unregister(this)

        mInboxPresenter.onPause()

    }

    private fun initUI() {

        swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent)

        swipe_refresh_layout.setOnRefreshListener {
            mInboxPresenter.onRefresh()
        }

        // TODO custom adapter
        // https://github.com/stfalcon-studio/ChatKit/blob/master/docs/COMPONENT_DIALOGS_LIST.MD
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
        dialogsListAdapter?.setOnDialogClickListener({

            it.unreadCnt = 0

            val author = it.users.first()
            val chatId = it.id.toLong()
            val recipientId = author.id.toLong()
            val recipientName = author.name

            ChatActivityArgs(
                    chatId = chatId,
                    recipientId = recipientId,
                    recipientName = recipientName)
                    .launch(this)

        })

        dialogs_list.setAdapter(dialogsListAdapter)

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onMessageEvent(event: NewMessageEvent) {

        mInboxPresenter.onMessage()

    }


}
