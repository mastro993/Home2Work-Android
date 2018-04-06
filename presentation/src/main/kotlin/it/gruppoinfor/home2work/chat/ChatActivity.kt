package it.gruppoinfor.home2work.chat

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
import com.stfalcon.chatkit.utils.DateFormatter
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.BaseActivity
import it.gruppoinfor.home2work.singlechat.SingleChatActivityLauncher
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
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class ChatActivity : BaseActivity<ChatViewModel, ChatVMFactory>() {

    private var dialogsListAdapter: DialogsListAdapter<Chat>? = null

    override fun getVMClass(): Class<ChatViewModel> {
        return ChatViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbox)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh_layout.setOnRefreshListener {
            viewModel.refreshChatList()
        }

        dialogsListAdapter = DialogsListAdapter(R.layout.item_dialog_custom, ImageLoader { imageView, url ->
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

            SingleChatActivityLauncher(
                    chatId = chatId,
                    recipientId = recipientId,
                    recipientName = recipientName)
                    .launch(this)

        })


        dialogsListAdapter?.setDatesFormatter(object : DateFormatter.Formatter {
            override fun format(date: Date?): String {

                var difference = Date().time - date!!.time
                val elapsedDays = difference / TimeUnit.DAYS.toMillis(1)
                difference %= TimeUnit.DAYS.toMillis(1)

                return when {
                    elapsedDays > 1 -> {
                        val sdfDate = SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN)
                        sdfDate.format(date)

                    }
                    elapsedDays > 0 -> "Ieri"
                    else -> {
                        val sdfTime = SimpleDateFormat("HH:mm", Locale.ITALIAN)
                        sdfTime.format(date)
                    }
                }
            }
        })
        dialogs_list.setAdapter(dialogsListAdapter)

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

    private fun handleViewState(state: ChatViewState?) {

        state?.let {

            swipe_refresh_layout.isRefreshing = it.isRefreshing
            status_view.setScreenState(state.screenState)

            state.chatList?.let {
                dialogsListAdapter?.setItems(it)
            }

        }

    }


}
