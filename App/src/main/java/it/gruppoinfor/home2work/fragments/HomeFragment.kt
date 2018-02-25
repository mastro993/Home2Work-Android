package it.gruppoinfor.home2work.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.custom.InboxIcon


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


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_home, menu)

        val item = menu.findItem(R.id.action_messages)
        inboxIcon = item.actionView as InboxIcon

        inboxIcon.setCount(4)
    }

    companion object {
        lateinit var inboxIcon: InboxIcon
    }


}
