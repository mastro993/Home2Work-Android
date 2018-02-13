package it.gruppoinfor.home2work.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import butterknife.ButterKnife
import butterknife.Unbinder
import it.gruppoinfor.home2work.R

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {


    private var mUnbinder: Unbinder? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        mUnbinder = ButterKnife.bind(this, rootView)
        setHasOptionsMenu(true)
        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_home, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // TODO search activity
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        mUnbinder!!.unbind()
        super.onDestroyView()
    }
}// Required empty public constructor
