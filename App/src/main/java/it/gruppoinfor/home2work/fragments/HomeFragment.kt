package it.gruppoinfor.home2work.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.custom.InboxIcon
import it.gruppoinfor.home2workapi.HomeToWorkClient


class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshInbox()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_home, menu)

        val item = menu.findItem(R.id.action_messages)
        inboxIcon = item.actionView as InboxIcon
    }

    private fun refreshInbox() {
        HomeToWorkClient.getInstance().refreshUserChatList(OnSuccessListener {
            inboxIcon.setCount(it.count { it.unreadCnt > 0 })
        }, OnFailureListener {

        })
    }

    companion object {
        lateinit var inboxIcon: InboxIcon
    }


}
