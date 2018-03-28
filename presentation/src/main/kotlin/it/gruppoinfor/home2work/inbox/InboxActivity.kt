package it.gruppoinfor.home2work.inbox

import android.app.NotificationManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.dialogs.DialogsList
import com.stfalcon.chatkit.dialogs.DialogsListAdapter
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.chat.ChatActivityLancher
import it.gruppoinfor.home2work.common.PicassoCircleTransform
import it.gruppoinfor.home2work.common.events.NewMessageEvent
import it.gruppoinfor.home2work.common.extensions.showToast
import it.gruppoinfor.home2work.common.services.MessagingService
import it.gruppoinfor.home2work.common.views.ScreenStateView
import it.gruppoinfor.home2work.di.DipendencyInjector
import it.gruppoinfor.home2work.entities.Chat
import kotlinx.android.synthetic.main.activity_inbox.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


class InboxActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: InboxVMFactory
    @Inject
    lateinit var imageLoader: it.gruppoinfor.home2work.common.ImageLoader

    private lateinit var viewModel: InboxViewModel
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var dialogsList: DialogsList
    private lateinit var statusView: ScreenStateView

    private var dialogsListAdapter: DialogsListAdapter<Chat>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbox)

        DipendencyInjector.createInboxComponent().inject(this)
        viewModel = ViewModelProvider(this, factory).get(InboxViewModel::class.java)

        swipeRefreshLayout = swipe_refresh_layout
        dialogsList = dialogs_list
        statusView = status_view


        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshChatList()
        }

        dialogsListAdapter = DialogsListAdapter(ImageLoader { imageView, url ->
            imageLoader.load(
                    url = url,
                    imageView = imageView,
                    transformation = PicassoCircleTransform(),
                    placeholder = R.drawable.ic_avatar_placeholder)
        })
        dialogsListAdapter?.setOnDialogClickListener({

            it.unreadCnt = 0

            val author = it.users.first()
            val chatId = it.id
            val recipientId = author.id.toLong()
            val recipientName = author.name

            ChatActivityLancher(
                    chatId = chatId,
                    recipientId = recipientId,
                    recipientName = recipientName)
                    .launch(this)

        })

        dialogsList.setAdapter(dialogsListAdapter)

        observeViewState()

        viewModel.getChatList()

    }

    override fun onResume() {
        super.onResume()

        viewModel.silentRefreshChatList()

        // Rimuovo eventuali notitifiche per nuovi messaggi ricevuti
        val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotifyMgr.cancel(MessagingService.MESSAGING_NOTIFICATION_ID)


        EventBus.getDefault().register(this)

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

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        DipendencyInjector.releaseInboxComponent()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: NewMessageEvent) {

        viewModel.silentRefreshChatList()

    }


    private fun observeViewState() {

        viewModel.errorState.observe(this, Observer {
            it?.let {
                showToast(it)
            }
        })
        viewModel.viewState.observe(this, Observer {
            handleViewState(it)
        })

    }

    private fun handleViewState(state: InboxViewState?) {

        state?.let {

            swipeRefreshLayout.isRefreshing = it.isRefreshing
            statusView.setScreenState(state.screenState)

            state.chatList?.let {
                dialogsListAdapter?.setItems(it)
            }

        }

    }


}
