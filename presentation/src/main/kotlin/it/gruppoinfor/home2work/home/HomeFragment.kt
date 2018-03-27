package it.gruppoinfor.home2work.home

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.events.NewMessageEvent
import it.gruppoinfor.home2work.common.views.InboxIconView
import it.gruppoinfor.home2work.di.DipendencyInjector
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


class HomeFragment : Fragment() {

    @Inject
    lateinit var factory: HomeVMFactory

    private lateinit var viewModel: HomeViewModel
    private var inboxIconView: InboxIconView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DipendencyInjector.createHomeComponent().inject(this)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)


        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
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

    override fun onDestroy() {
        super.onDestroy()
        DipendencyInjector.releaseHomeComponent()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_home, menu)

        val item = menu.findItem(R.id.action_messages)
        inboxIconView = item.actionView as InboxIconView

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: NewMessageEvent) {

        viewModel.getInboxCount()

    }


}
