package it.gruppoinfor.home2work.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.*
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.BaseFragment
import it.gruppoinfor.home2work.common.events.NewMessageEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class HomeFragment : BaseFragment<HomeViewModel, HomeVMFactory>() {

    private var chatIconView: ChatIconView? = null

    override fun getVMClass(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.viewState.observe(this, Observer {
            it?.let {
                chatIconView?.setCount(it.unreadChatCount)
            }
        })
    }

    override fun onResume() {
        super.onResume()

        viewModel.getInboxCount()

        EventBus.getDefault().register(this)

    }

    override fun onPause() {
        super.onPause()

        EventBus.getDefault().unregister(this)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_home, menu)

        val item = menu.findItem(R.id.action_messages)
        chatIconView = item.actionView as ChatIconView

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: NewMessageEvent) {

        viewModel.getInboxCount()

    }


}
