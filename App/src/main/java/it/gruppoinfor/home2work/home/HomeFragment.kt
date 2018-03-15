package it.gruppoinfor.home2work.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.chat.InboxIconView
import it.gruppoinfor.home2work.firebase.NewMessageEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class HomeFragment : Fragment(), HomeView {

    private val mHomePresenter: HomePresenter = HomePresenterImpl(this)
    private var inboxIconView: InboxIconView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        super.onResume()

        mHomePresenter.onResume()

        EventBus.getDefault().register(this)

    }

    override fun onPause() {
        super.onPause()

        EventBus.getDefault().unregister(this)

        mHomePresenter.onPause()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_home, menu)

        val item = menu.findItem(R.id.action_messages)
        inboxIconView = item.actionView as InboxIconView

    }

    override fun refreshInboxCounter(count: Int) {
        inboxIconView?.setCount(count)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onMessageEvent(event: NewMessageEvent) {

        mHomePresenter.onNewMessage()

    }


}
