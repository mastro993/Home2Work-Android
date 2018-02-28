package it.gruppoinfor.home2work.activities

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
import it.gruppoinfor.home2work.utils.Const
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.inbox.Author
import it.gruppoinfor.home2workapi.inbox.Chat
import it.gruppoinfor.home2workapi.inbox.Message
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : AppCompatActivity() {

    private lateinit var chat: Chat
    var adapter: MessagesListAdapter<Message>? = null

    private val mMessageReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            val chatId = intent.getLongExtra(Const.CHAT_ID, 0)

            if (chat.chatId == chatId) {

                val message = Message()
                message.messageText = intent.getStringExtra(Const.TEXT)
                message.messageUser = chat.users.first() as Author

                adapter?.addToStart(message, true)

            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        if (intent.hasExtra(Const.EXTRA_CHAT)) {
            chat = intent.getSerializableExtra(Const.EXTRA_CHAT) as Chat
            initUI()
        } else {
            Toast.makeText(this, "Chat non valida", Toast.LENGTH_SHORT).show()
            finish()
        }

    }


    override fun onStart() {
        super.onStart()

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,
                IntentFilter(Const.NEW_MESSAGE_RECEIVED)
        )

        refreshMessages()

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

    private fun initUI() {

        status_view.loading()

        title = chat.users.first().name

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

            HomeToWorkClient.sendMessageToChat(chat.chatId, message.text, OnSuccessListener {

            }, OnFailureListener {

                Toast.makeText(this, "Impossibile inviare il messaggio al momento", Toast.LENGTH_SHORT).show()

            })

            true
        })

    }

    private fun refreshMessages() {
        HomeToWorkClient.getChatMessageList(chat.chatId, OnSuccessListener {

            adapter?.addToEnd(it, true)
            status_view.done()

        }, OnFailureListener {

            status_view.error("Impossibile ottenere i messaggi della conversazione")

        })
    }


}
