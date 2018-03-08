package it.gruppoinfor.home2work.chat

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
import com.stfalcon.chatkit.messages.MessagesListAdapter
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.firebase.MessagingService
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.chat.Author
import it.gruppoinfor.home2workapi.chat.Chat
import it.gruppoinfor.home2workapi.chat.Message
import it.gruppoinfor.home2workapi.user.User
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : AppCompatActivity() {

    private var chat: Chat? = null
    var adapter: MessagesListAdapter<Message>? = null

    private val mMessageReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            val chatId = intent.getLongExtra(MessagingService.CHAT_ID, 0)

            if (chat != null && chat?.chatId == chatId) {

                val message = Message()
                message.messageText = intent.getStringExtra(MessagingService.TEXT)
                message.messageUser = chat?.users?.first() as Author

                adapter?.addToStart(message, true)

            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initUI()

        when {
            intent.hasExtra(EXTRA_CHAT) -> {
                chat = intent.getSerializableExtra(EXTRA_CHAT) as Chat
                title = chat?.users?.first()?.name
                getMessages()
            }
            intent.hasExtra(EXTRA_NEW_CHAT) -> {
                // TODO sistemare
                val recipient = intent.getSerializableExtra(EXTRA_NEW_CHAT)
                title = recipient.toString()
                newChat(recipient as User)
            }
            else -> invalidChat()
        }

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

    private fun initUI() {

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

        input.setInputListener({

            val message = Message()
            message.messageText = it.toString().trim()
            message.messageUser = Author(HomeToWorkClient.user?.id)
            adapter?.addToStart(message, true)

            HomeToWorkClient.sendMessageToChat(chat!!.chatId, message.text, OnSuccessListener {

            }, OnFailureListener {

                Toast.makeText(this, "Impossibile inviare il messaggio al momento", Toast.LENGTH_SHORT).show()

            })

            true
        })

        input.inputEditText.hint = "Scrivi un messaggio"

    }

    private fun getMessages() {

        status_view.loading()

        HomeToWorkClient.getChatMessageList(chat!!.chatId, OnSuccessListener {list->

            adapter?.addToEnd(list, true)
            refreshUI()

        }, OnFailureListener {

            status_view.error("Impossibile ottenere i messaggi della conversazione")
            it.printStackTrace()

        })

    }

    private fun newChat(recipient: User) {

        status_view.loading()

        // TODO sistemare lato server
        HomeToWorkClient.newChat(recipient.id, OnSuccessListener {

            chat = it
            getMessages()

        }, OnFailureListener {

            status_view.error("Impossibile avviare una nuova conversazione al momento")
            it.printStackTrace()

        })

    }

    private fun refreshUI(){

        status_view.done()

    }

    private fun invalidChat() {
        Toast.makeText(this, "Chat non valida", Toast.LENGTH_SHORT).show()
        finish()
    }

    companion object {
        const val EXTRA_CHAT = "extra_chat"
        const val EXTRA_NEW_CHAT = "extra_new_chat"
    }


}
