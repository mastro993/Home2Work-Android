package it.gruppoinfor.home2work.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

import java.util.ArrayList

import butterknife.ButterKnife
import butterknife.Unbinder
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2workapi.model.Share

class ProfileFragmentDashboard : Fragment() {

    private var mUnbinder: Unbinder? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_profile_dashboard, container, false) as FrameLayout
        mUnbinder = ButterKnife.bind(this, root)
        initUI()
        return root
    }

    private fun initUI() {

    }

    override fun onDestroyView() {
        mUnbinder!!.unbind()
        super.onDestroyView()
    }

}// Required empty public constructor
