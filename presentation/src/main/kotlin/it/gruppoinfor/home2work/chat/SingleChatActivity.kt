package it.gruppoinfor.home2work.chat

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.MenuItem
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessagesListAdapter
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.BaseActivity
import it.gruppoinfor.home2work.common.PicassoCircleTransform
import it.gruppoinfor.home2work.common.events.NewMessageEvent
import it.gruppoinfor.home2work.common.extensions.showToast
import it.gruppoinfor.home2work.entities.ChatMessage
import kotlinx.android.synthetic.main.activity_chat.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SingleChatActivity : BaseActivity<SingleChatViewModel, SingleChatVMFactory>() {


    private val args by lazy {
        SingleChatActivityLauncher.deserializeFrom(intent)
    }

    var adapter: MessagesListAdapter<ChatMessage>? = null

    override fun getVMClass(): Class<SingleChatViewModel> {
        return SingleChatViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = args.recipientName


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
        viewModel.viewStateSingle.observe(this, Observer {
            handleViewState(it)
        })

        viewModel.messageSent.observe(this, Observer {
            // TODO sistemare aggiunta doppia messaggi
            adapter?.addToStart(it, true)
        })

        viewModel.messageReceived.observe(this, Observer {
            adapter?.addToStart(it, true)
        })

    }

    private fun handleViewState(stateSingle: SingleChatViewState?) {

        stateSingle?.let {

            status_view.setScreenState(stateSingle.screenState)

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
