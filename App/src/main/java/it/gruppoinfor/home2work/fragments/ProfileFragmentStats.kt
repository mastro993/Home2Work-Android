package it.gruppoinfor.home2work.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import it.gruppoinfor.home2work.R


class ProfileFragmentStats : Fragment() {

    @BindView(R.id.regdate_text_view)
    internal var regdateTextView: TextView? = null
    @BindView(R.id.shares_text_view)
    internal var sharesTextView: TextView? = null
    @BindView(R.id.shared_distance_text_view)
    internal var sharedDistanceTextView: TextView? = null
    @BindView(R.id.saved_gas_text_view)
    internal var savedGasTextView: TextView? = null
    @BindView(R.id.saved_emissions_text_view)
    internal var savedEmissionsTextView: TextView? = null
    private var mUnbinder: Unbinder? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_profile_stats, container, false) as NestedScrollView
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
