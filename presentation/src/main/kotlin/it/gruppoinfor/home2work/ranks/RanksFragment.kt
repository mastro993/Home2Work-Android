package it.gruppoinfor.home2work.ranks


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import it.gruppoinfor.home2work.R


/**
 * A simple [Fragment] subclass.
 */
class RanksFragment : Fragment() {


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ranks, container, false)
    }

} // Required empty public constructor
