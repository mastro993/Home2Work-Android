package it.gruppoinfor.home2work.chat

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessagesListAdapter
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.PicassoCircleTransform
import it.gruppoinfor.home2work.common.events.NewMessageEvent
import it.gruppoinfor.home2work.common.extensions.showToast
import it.gruppoinfor.home2work.common.user.LocalUserData
import it.gruppoinfor.home2work.common.views.ScreenStateView
import it.gruppoinfor.home2work.di.DipendencyInjector
import it.gruppoinfor.home2work.entities.ChatMessage
import kotlinx.android.synthetic.main.activity_chat.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class ChatActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ChatVMFactory
    @Inject
    lateinit var imageLoader: it.gruppoinfor.home2work.common.ImageLoader
    @Inject
    lateinit var localUserData: LocalUserData

    lateinit var viewModel: ChatViewModel
    lateinit var statusView: ScreenStateView

    private val args by lazy {
        ChatActivityLauncher.deserializeFrom(intent)
    }

    var adapter: MessagesListAdapter<ChatMessage>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        DipendencyInjector.createChatComponent().inject(this)
        viewModel = ViewModelProvider(this, factory).get(ChatViewModel::class.java)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = args.recipientName
        statusView = status_view


        adapter = MessagesListAdapter(localUserData.user?.id.toString(), ImageLoader { imageView, url ->
            imageLoader.load(
                    url = url,
                    imageView = imageView,
                    transformation = PicassoCircleTransform(),
                    placeholder = R.drawable.ic_avatar_placeholder)
        })
        messages_list.setAdapter(adapter)

        input.setInputListener({
            viewModel.sendMessage(it.toString())
            true
        })
        input.inputEditText.hint = "Scrivi un messaggio"

        when {
            args.chatId != 0L -> {
                viewModel.chatId = args.chatId
                viewModel.getMessageList()
            }
            args.recipientId != 0L -> {
                viewModel.userId = args.recipientId
                viewModel.createChat()
            }
        }

        observeViewState()


    }

    override fun onResume() {
        super.onResume()

        EventBus.getDefault().register(this)

    }

    override fun onPause() {
        super.onPause()

        EventBus.getDefault().unregister(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        DipendencyInjector.releaseChatComponent()
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

    private fun observeViewState() {

        viewModel.errorState.observe(this, Observer {
            it?.let {
                showToast(it)
            }
        })
        viewModel.viewState.observe(this, Observer {
            handleViewState(it)
        })

        viewModel.messageSent.observe(this, Observer {
            adapter?.addToStart(it, true)
        })

    }

    private fun handleViewState(state: ChatViewState?) {

        state?.let {

            statusView.setScreenState(state.screenState)

            it.messageList?.let {
                adapter?.addToEnd(it, true)
            }

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: NewMessageEvent) {

        viewModel.onNewMessageEvent(event)

    }


}
