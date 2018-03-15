package it.gruppoinfor.home2work.chat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessagesListAdapter
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.firebase.NewMessageEvent
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.chat.Author
import it.gruppoinfor.home2workapi.chat.Message
import kotlinx.android.synthetic.main.activity_chat.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class ChatActivity : AppCompatActivity(), ChatView {

    private val mChatPresenter: ChatPresenter = ChatPresenterImpl(this)

    private val args by lazy {
        ChatActivityArgs.deserializeFrom(intent)
    }

    var adapter: MessagesListAdapter<Message>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initUI()

        title = args.recipientName

        mChatPresenter.setChatId(args.chatId)
        mChatPresenter.setRecipientId(args.recipientId)

    }

    override fun onResume() {
        super.onResume()

        EventBus.getDefault().register(this)

        mChatPresenter.onResume()

    }

    override fun onPause() {
        super.onPause()

        EventBus.getDefault().unregister(this)

        mChatPresenter.onPause()

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

    override fun onMessageSent() {
        // TODO messaggio inviato con successo
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onMessageEvent(event: NewMessageEvent) {

        mChatPresenter.onNewMessageEvent(event)

    }

    override fun onLoading() {
        status_view.loading()
    }

    override fun onLoadingError(errorMessage: String) {
        status_view.error(errorMessage)
    }

    override fun onNewMessage(message: Message) {
        status_view.done()
        adapter?.addToStart(message, true)
    }

    override fun setItems(list: List<Message>) {
        if (list.isEmpty()) {
            status_view.empty("Scrivi tu il primo messaggio")
        } else {
            adapter?.addToEnd(list, true)
            status_view.done()
        }
    }

    override fun onMessageSentError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun initUI() {

        // TODO custom adapter
        // https://github.com/stfalcon-studio/ChatKit/blob/master/docs/COMPONENT_MESSAGES_LIST.md
        adapter = MessagesListAdapter(HomeToWorkClient.user?.id.toString(), ImageLoader { imageView, url ->
            val requestOptions = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .circleCrop()
                    .placeholder(R.drawable.ic_avatar_placeholder)

            Glide.with(this@ChatActivity)
                    .load(url)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(requestOptions)
                    .into(imageView)
        })
        messages_list.setAdapter(adapter)

        input.setInputListener(this::sendMessage)
        input.inputEditText.hint = "Scrivi un messaggio"

    }

    private fun sendMessage(text: CharSequence): Boolean {
        status_view.done()

        val message = Message()
        message.messageText = text.toString().trim()
        message.messageUser = Author(HomeToWorkClient.user?.id)
        adapter?.addToStart(message, true)

        mChatPresenter.sendMessage(message)

        return true
    }


}
